/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObject;

/** Validates the result of invocation
 * @author Grid Dynamics
 * @n
 * @par Details:
 * @details Simplified type of collector. @n
 * Validates the result of invocation of specified query and endpoint. Save validation result to database. @n
 * Validators execute one by one. If one fails, no other will be executed. @n
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Validators_group */
public abstract class ResponseValidator<Q, E, R> extends KernelSideObject {

    /** Default constructor for validators
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details This constructor will be called by validator provider, which creates validator instances
     *
     * @param taskId        - id of current task
     * @param sessionId     - id of current session
     * @param kernelContext - context for current Node */
    public ResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    /** Returns the name of validator
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details Returns the name of validator. This name will be displayed at webUI and jagger report.
     *
     * @return the name of validator */
    public abstract String getName();

    /** Validates the result of invocation
     * @author Grid Dynamics
     * @n
     * @par Details:
     * @details  Validates the result of invocation with specified query and endpoint. If return false current request to SUT will be marked as failed.
     *
     * @param query     - the query of current invocation
     * @param endpoint  - the endpoint of current invocation
     * @param result    - the result of invocation
     * @param duration  - the duration of invocation
     *
     * @return true if validation is successful */
    public abstract boolean validate(Q query, E endpoint, R result, long duration);

}

/// @page MetricsValidators Validators
/// @details
/// @n
/// Validation of SUT responses is provided by Jagger components Validators. They verify responses from the SUT and decide whether responses are valid or not.
/// Every response can be validated by multiple validators. One after another. If one of the validators in the chain sets FAIL status to the response, this request
/// is considered failed. This will affect @ref MetricsPerformance "standard performance metrics": success rate and number of failures. @n
///
/// @par Java doc for validators and examples
/// @ref Main_Validators_group
///
/// @par Example of validator
/// We will create a custom validator provider. This provider is returning an instance of validator. Depending on the setup our validator will verify http response code or always
/// return true.
/// @include  ExampleResponseValidatorProvider.java
/// We will add created validator to a particular test. You can add multiple validators to the same test. htey will be executed in the same sequence like they are added
/// @dontinclude  JLoadScenarioProvider.java
/// @skip  begin: following section is used for docu generation - example of the invocation listener
/// @until end: following section is used for docu generation - example of the invocation listener

// *************************************
// not a part of the documentation below

/// @defgroup Main_Validators_group Validators implementations and examples
/// @details @ref MetricsValidators
