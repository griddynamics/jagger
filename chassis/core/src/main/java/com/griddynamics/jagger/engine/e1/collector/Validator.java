package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.Flushable;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObject;
import com.griddynamics.jagger.storage.KeyValueStorage;
import com.griddynamics.jagger.storage.Namespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.griddynamics.jagger.engine.e1.collector.CollectorConstants.RESULT;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/15/13
 * Time: 3:27 PM
 * To change this template use File | Settings | File Templates.
 */
public class Validator extends KernelSideObject implements Flushable{
    private static final Logger log = LoggerFactory.getLogger(Validator.class);

    private ResponseValidator validator;
    private String displayName;
    private int invoked = 0;
    private int failed = 0;

    public Validator(String taskId, String sessionId, NodeContext kernelContext, ResponseValidator validator, String displayName) {
        super(taskId, sessionId, kernelContext);
        this.validator = validator;
        this.displayName = displayName;
    }

    public String getName() {
        return validator.getName();
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean validate(Object query, Object endpoint, Object result, long duration) {
        invoked++;
        boolean success = validator.validate(query, endpoint, result, duration);
        if (!success){
            failed++;
            return false;
        }

        return true;
    }

    public ResponseValidator getValidator(){
        return validator;
    }

    private Namespace namespace() {
        return Namespace.of(sessionId, taskId, "ValidationCollector",
                kernelContext.getId().toString());
    }

    @Override
    public void flush() {
        log.debug("Going to store validation result in key-value storage");

        Namespace namespace = namespace();

        KeyValueStorage keyValueStorage = kernelContext.getService(KeyValueStorage.class);

        keyValueStorage.put(namespace, RESULT, ValidationResult.create(validator.getName(), displayName, invoked, failed));

        log.debug("invoked {} failed {}", invoked, failed);
    }
}
