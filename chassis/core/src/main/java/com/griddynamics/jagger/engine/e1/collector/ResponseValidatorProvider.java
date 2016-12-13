package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * Provides instances of a specified ${@link ResponseValidator} class
 * using reflection and a mandatory constructor for ${@link ResponseValidator} instances
 * @n
 * Created by Andrey Badaev
 * Date: 13/12/16
 */
public class ResponseValidatorProvider implements KernelSideObjectProvider<ResponseValidator<Object, Object, Object>> {
    
    
    private final Class<? extends ResponseValidator> clazz;
    
    public ResponseValidatorProvider(Class<? extends ResponseValidator> clazz) {this.clazz = clazz;}
    
    public static ResponseValidatorProvider of(Class<? extends ResponseValidator> clazz) {
        return new ResponseValidatorProvider(clazz);
    }
    
    @Override
    public ResponseValidator<Object, Object, Object> provide(String sessionId,
                                                             String taskId,
                                                             NodeContext kernelContext) {
        try {
            Constructor<? extends ResponseValidator> constructor = clazz.getConstructor(String.class, String.class, NodeContext.class);
            return constructor.newInstance(sessionId, taskId, kernelContext);
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
