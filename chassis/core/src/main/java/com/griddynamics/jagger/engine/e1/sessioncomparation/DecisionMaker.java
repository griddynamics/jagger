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
package com.griddynamics.jagger.engine.e1.sessioncomparation;

import com.google.common.collect.Multimap;

// @todo edit please
/** Make a decision(OK, WARNING, FATAL, ERROR) by current test
 * @author Dmitry Kotlyarov
 * @n
 * @par Details:
 * @details Make a decision based on comparison between current test and test from baseline session
 *
 * @ingroup Main_DecisionMakers_Base_group */
public interface DecisionMaker {

    /** Returns decision
     * @author Dmitry Kotlyarov
     * @n
     *
     * @param verdicts - verdicts of comparison between current test and test from baseline session
     *
     * @return decision(OK, WARNING, FATAL, ERROR) */
    Decision makeDecision(Multimap<String, Verdict> verdicts);

}

/* **************** How to customize decision maker ************************* */
/// @defgroup Main_HowToCustomizeDecisionMakers_group Custom decision makers
///
/// @details  Under construction
/// @todo implement docu
///