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

package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.scenario.KernelSideObject;

/** ??? Some short description
 * @author ???
 * @n
 * @par Details:
 * @details ???
 *
 * @param <Q> - Query type
 * @param <R> - Result type
 * @param <E> - Endpoint type
 *
 * @ingroup Main_Collectors_Base_group */
public abstract class ResponseValidator<Q, E, R> extends KernelSideObject {

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @param taskId        - ???
     *  @param sessionId     - ???
     *  @param kernelContext - ??? */
    public ResponseValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @return ??? */
    public abstract String getName();

    /** ??? Some short description
     * @author ???
     * @n
     * @par Details:
     * @details ???
     *
     *  @param query     - ???
     *  @param endpoint  - ???
     *  @param result    - ???
     *  @param duration  - ???
     *
     *  @return ??? */
    public abstract boolean validate(Q query, E endpoint, R result, long duration);

}

/* **************** How to customize collector ************************* */
/// @defgroup Main_HowToCustomizeCollectors_group Custom collectors
///
/// @details
/// @todo finish docu section Custom collectors