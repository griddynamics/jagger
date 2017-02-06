package com.griddynamics.jagger.engine.e1.process;

import com.google.common.util.concurrent.Service;

public interface WorkloadService extends Service {

    Integer getStartedSamples();
    Integer getFinishedSamples();
    long getEmptyTransactions();
    void changeDelay(int delay);
}
