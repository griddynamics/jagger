package com.griddynamics.jagger.invoker.v2;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.invoker.Invoker;

/**
 * Implementation of {@link Provider<Invoker>} which uses a default no-arguments constructor
 * of provided subclass of {@link Invoker} to create it's instances.
 * @n
 * Created by Andrey Badaev
 * Date: 30/12/16
 */
public class DefaultInvokerProvider implements Provider<Invoker> {
    
    private final Class<? extends Invoker> invokerClass;
    
    public DefaultInvokerProvider(final Class<? extends Invoker> invokerClass) {this.invokerClass = invokerClass;}
    
    public static DefaultInvokerProvider of(final Class<? extends Invoker> invokerClass) {
        return new DefaultInvokerProvider(invokerClass);
    }
    
    
    @Override
    public Invoker provide()  {
        try {
            return invokerClass.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new IllegalStateException(String.format("Error during triggering a default constructor for %s",
                                                          invokerClass), e);
        }
    }
}
