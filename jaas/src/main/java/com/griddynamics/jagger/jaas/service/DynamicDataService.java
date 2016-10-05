package com.griddynamics.jagger.jaas.service;

import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.contains;

import com.griddynamics.jagger.config.DataServiceConfig;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.jaas.storage.DbConfigEntityDao;
import com.griddynamics.jagger.jaas.storage.model.DbConfigEntity;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

import javax.annotation.PreDestroy;

/**
 * Provides {@link com.griddynamics.jagger.engine.e1.services.DataService} service
 * based on configuration described by {@link DbConfigEntity}
 * and handles storage for {@link DbConfigEntityDao} entities.
 */
@Service
public class DynamicDataService implements DbConfigEntityDao {

    private static final Logger LOGGER = LoggerFactory.getLogger(DynamicDataService.class);

    private final ExecutorService destroyerService = Executors.newSingleThreadExecutor(r -> new Thread(r, "DynamicDataServiceDestoyer"));

    private final ConcurrentMap<Long, AbstractApplicationContext> dataServiceContexts = new ConcurrentHashMap<>();
    private final DbConfigEntityDao jaasDao;
    private int dataServiceCacheSize = 10;

    public DynamicDataService(@Autowired DbConfigEntityDao jaasDao,
                              @Value("${jaas.data.service.cache.size:10}") int dataServiceCacheSize,
                              @Autowired DbConfigEntity defaultDbConfigEntity
    ) {
        this.jaasDao = jaasDao;
        if (dataServiceCacheSize > 0) {
            this.dataServiceCacheSize = dataServiceCacheSize;
        }

        if (jaasDao.readAll().isEmpty()) {
            LOGGER.info("Registering default jagger test db config: {}", defaultDbConfigEntity);
            jaasDao.create(defaultDbConfigEntity);
            getDataServiceFor(defaultDbConfigEntity.getId());
        }
    }

    @PreDestroy
    public void destroy() {
        destroyerService.shutdown();
    }

    protected void destroy(final AbstractApplicationContext context) {
        destroyerService.execute(() -> doDestroy(context));
    }

    protected void evictIfAboveThreshold() {
        destroyerService.execute(() -> {
            if (dataServiceContexts.size() >= dataServiceCacheSize) {
                doDestroy(dataServiceContexts.remove(dataServiceContexts.keySet().iterator().next()));
            }
        });
    }

    /**
     * To be called only inside {@link #destroyerService} workers
     * which guarantees serial eviction as soon as {@link #destroyerService} is single-threaded.
     *
     * @param context to be destroyed
     */
    private void doDestroy(final AbstractApplicationContext context) {
        if (context != null) {
            LOGGER.info("Destroying jagger db context...");
            context.destroy();
        }
    }

    public DataService getDataServiceFor(final Long configId) {
        return getDynamicContextFor(configId).getBean(DataService.class);
    }
    
    public ApplicationContext getDynamicContextFor(final Long configId) {
        Objects.requireNonNull(configId);
    
        ApplicationContext applicationContext = dataServiceContexts.get(configId);
        if (Objects.isNull(applicationContext)) {
            DbConfigEntity config = jaasDao.read(configId);
            if (Objects.isNull(config)) {
                return null;
            }
            evictIfAboveThreshold();
            applicationContext = dataServiceContexts.computeIfAbsent(configId, s -> initDataServiceContextFor(config));
        }
        return applicationContext;
    }

    protected AbstractApplicationContext initDataServiceContextFor(DbConfigEntity config) {
        LOGGER.debug("Initializing spring context for jagger test db config: {}", config);

        checkConnectionToDb(config);

        PropertiesPropertySource propertySource =
                new PropertiesPropertySource(this.getClass().getName(), extractPropsFrom(config));

        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
        applicationContext.getEnvironment().getPropertySources().addFirst(propertySource);
        applicationContext.register(DataServiceConfig.class);
        applicationContext.refresh();

        LOGGER.debug("Spring context has been initialized for jagger test db config: {}", config);
        return applicationContext;
    }

    private void checkConnectionToDb(DbConfigEntity config) {
        try (Connection connection = DriverManager.getConnection(config.getUrl(), config.getUser(), config.getPass())) {
            connection.isValid(1);
        } catch (SQLException e) {
            Throwable rootCause = ExceptionUtils.getRootCause(e);
            if (rootCause instanceof ConnectException && contains(rootCause.getMessage(), "Connection refused")) {
                LOGGER.error(format("Cannot establish connection to data base %s. ", config));
            }
            throw new RuntimeException(e);
        }
    }

    public Properties extractPropsFrom(DbConfigEntity config) {
        Properties configProps = new Properties();
        for (Field field : config.getClass().getDeclaredFields()) {
            JaggerPropertyName propertyName = field.getAnnotation(JaggerPropertyName.class);
            if (Objects.nonNull(propertyName)) {
                field.setAccessible(true);
                configProps.setProperty(propertyName.value(), (String) ReflectionUtils.getField(field, config));
            }
        }

        return configProps;
    }

    @Override
    public DbConfigEntity read(Long configId) {
        return jaasDao.read(configId);
    }

    @Override
    public List<DbConfigEntity> readAll() {
        return jaasDao.readAll();
    }

    @Override
    public void create(DbConfigEntity config) {
        jaasDao.create(config);
    }

    @Override
    public void update(DbConfigEntity config) {
        evictableOperation(jaasDao::update, config);
    }

    @Override
    public void createOrUpdate(DbConfigEntity config) {
        evictableOperation(jaasDao::createOrUpdate, config);
    }

    @Override
    public void delete(DbConfigEntity config) {
        evictableOperation(jaasDao::delete, config);
    }

    private void evictableOperation(Consumer<DbConfigEntity> op, DbConfigEntity config) {
        op.accept(config);
        destroy(dataServiceContexts.remove(config.getId()));
    }
}
