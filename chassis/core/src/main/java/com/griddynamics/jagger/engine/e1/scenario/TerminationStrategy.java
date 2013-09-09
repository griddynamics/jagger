/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
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

package com.griddynamics.jagger.engine.e1.scenario;

/// @todo Add an ability to use custom termination strategies
/** This class say when Jagger has to terminate workload
 * @author Mairbek Khadikov
 * @n
 * @par Details:
 * @details An object which decides when to terminate the test. The termination decision can be based on such params - time of execution, number of threads, finished samples and current samples.
 *
 * @ingroup Main_Terminators_Base_group */
public interface TerminationStrategy {

    /** Returns true if termination for the test is required
     * @author Mairbek Khadikov
     * @n
     * @par Details:
     * @details Termination strategy describes when test can be terminated. It can be based on a lot of params. For example test can be terminated when Jagger did an exact number of samples.
     *
     * @param status - current jagger execution status. Contains such info - number of threads, finished samples, current samples.
     *
     * @return true if termination is required */
    boolean isTerminationRequired(WorkloadExecutionStatus status);
}
