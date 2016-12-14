package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Default implementation of ${@link ResponseValidatorProvider} which provides instances of ${@link ResponseValidator}
 * using reflection and a mandatory constructor for ${@link ResponseValidator} instances
 * @n
 * Created by Andrey Badaev
 * Date: 13/12/16
 */
public class DefaultResponseValidatorProvider implements ResponseValidatorProvider {
    
    private final Class<? extends ResponseValidator<?, ?, ?>> clazz;
    
    public DefaultResponseValidatorProvider(Class<? extends ResponseValidator<?, ?, ?>> clazz) {
        this.clazz = clazz;
    }
    
    public static DefaultResponseValidatorProvider of(Class<? extends ResponseValidator<?, ?, ?>> clazz) {
        return new DefaultResponseValidatorProvider(clazz);
    }
    
    @Override
    public ResponseValidator<?, ?, ?> provide(String sessionId, String taskId, NodeContext kernelContext) {
        try {
            Constructor<? extends ResponseValidator> constructor = clazz.getConstructor(String.class, String.class, NodeContext.class);
            return constructor.newInstance(taskId, sessionId, kernelContext);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
