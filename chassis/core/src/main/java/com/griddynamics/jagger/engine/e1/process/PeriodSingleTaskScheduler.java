package com.griddynamics.jagger.engine.e1.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * Loop scheduler that can execute one task with given period.
 * If you add new task - previous will be canceled.
 * You can change period of process at runtime.
 */
public class PeriodSingleTaskScheduler {

    private ScheduledExecutorService loopExecutor;
    private ExecutorService taskExecutor;

    /**
     * Last started schedule process, saved to be able to stop it */
    private ScheduledFuture lastStartedLoopProcess = null;
    private static final int DEFAULT_CORE_POOL_SIZE = 2;

    /**
     * Last started configuration */
    private volatile Configuration currentConfiguration;

    Logger log = LoggerFactory.getLogger(PeriodSingleTaskScheduler.class);

    public PeriodSingleTaskScheduler() {
        loopExecutor = Executors.newScheduledThreadPool(DEFAULT_CORE_POOL_SIZE);
        taskExecutor = Executors.newFixedThreadPool(5);
    }


    /**
     * Schedule command at fixed rate.
     * Cancel previously started process.
     *
     * @param command command to be executed every period
     * @param initialDelay initialDelay before executing command
     * @param period period
     * @param unit time unit for period, initialDelay
     */
    public synchronized void scheduleAtFixedRate(final Runnable command,
                                    long initialDelay,
                                    long period,
                                    TimeUnit unit) {

        Configuration newConfiguration = new Configuration(command, initialDelay, period, unit);

        if (!newConfiguration.equals(currentConfiguration)) {

            log.debug("Schedule new task {}", newConfiguration);
            currentConfiguration = newConfiguration;
            // cancel task if running
            if (lastStartedLoopProcess != null) {
                // stop previous loop process, but do not interrupt
                lastStartedLoopProcess.cancel(false);
            }

            lastStartedLoopProcess = loopExecutor.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    taskExecutor.submit(command);
                }
            }, initialDelay, period, unit);
        }
    }


    /**
     * Shutdown scheduler
     */
    public synchronized void shutdown() {
        loopExecutor.shutdownNow();
        taskExecutor.shutdownNow();
    }


    /**
     * Disable Task
     */
    public synchronized void clear() {
        if (lastStartedLoopProcess != null) {
            lastStartedLoopProcess.cancel(false);
        }
        currentConfiguration = null;
    }


    /**
     * Represents current configuration of loop process
     */
    private class Configuration {

        private final Runnable command;
        private final long initialDelay;
        private final long period;
        private final TimeUnit unit;

        public Configuration(Runnable command, long delay, long period, TimeUnit unit) {


            this.command = command;
            this.initialDelay = delay;
            this.period = period;
            this.unit = unit;
        }

        public Runnable getCommand() {
            return command;
        }

        public long getInitialDelay() {
            return initialDelay;
        }

        public long getPeriod() {
            return period;
        }

        public TimeUnit getUnit() {
            return unit;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Configuration that = (Configuration) o;

            if (initialDelay != that.initialDelay) return false;
            if (period != that.period) return false;
            if (command != null ? !command.equals(that.command) : that.command != null) return false;
            if (unit != that.unit) return false;

            return true;
        }
    }
}
