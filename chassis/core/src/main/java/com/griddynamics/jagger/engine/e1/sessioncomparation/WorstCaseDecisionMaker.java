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

/** Returns the worst decision of comparisons
 * @author Dmitry Kotlyarov
 * @n
 *
 * @ingroup Main_DecisionMakers_group */
public class WorstCaseDecisionMaker implements DecisionMaker {

    /** Returns the worst decision of comparisons
     * @author Dmitry Kotlyarov
     * @n
     *
     * @param verdicts - verdicts of comparisons between current test and test from baseline session
     *
     * @return the worst decision */
    @Override
    public Decision makeDecision(Multimap<String, Verdict> verdicts) {
        Decision worstResult = Decision.OK;
        for (String feature : verdicts.keySet()) {
            for (Verdict verdict : verdicts.get(feature)) {
                Decision decision = verdict.getDecision();
                if (decision.ordinal() > worstResult.ordinal()) {
                    worstResult = decision;
                }
            }

        }

        return worstResult;
    }

}
