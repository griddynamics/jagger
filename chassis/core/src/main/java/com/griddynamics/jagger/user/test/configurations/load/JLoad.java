package com.griddynamics.jagger.user.test.configurations.load;

/**
 * This type of load imitates an exact number of requests per second. Where request is invoke from Jagger.
 * By using attribute 'requestsPerSecond', you can configure a number of requests. Attribute 'maxLoadThreads'
 * says what is the maximum number of threads Jagger engine is allowed to create, to provide the requested load.
 * By default it equals 4000. If attribute 'warmUpTimeInSeconds' is set,
 * load will increase from 0 to the value for this time.
 */
public interface JLoad {
}
