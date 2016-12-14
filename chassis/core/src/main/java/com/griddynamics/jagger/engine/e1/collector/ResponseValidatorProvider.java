package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.engine.e1.scenario.KernelSideObjectProvider;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * Provides instances of a ${@link ResponseValidator} class
 * @n
 * Created by Andrey Badaev
 * Date: 14/12/16
 */
public interface ResponseValidatorProvider extends KernelSideObjectProvider<ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse>> {
}
