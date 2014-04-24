package com.griddynamics.jagger.invoker;

import com.griddynamics.jagger.engine.e1.Provider;

/**
 * Created by kgribov on 2/18/14.
 */
public class DefaultQueryProcessorProvider implements Provider<QueryProcessor> {
    @Override
    public QueryProcessor provide() {
        return new QueryProcessor() {
            @Override
            public StepInfo preProcess(StepInfo stepInfo) {
                return stepInfo;
            }

            @Override
            public StepInfo postProcess(StepResult currentResult) {
                return currentResult.getStepInfo();
            }

        };
    }
}
