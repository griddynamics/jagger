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
package com.griddynamics.jagger.engine.e1.sessioncomparation;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.util.Decision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;

import java.util.List;

public class ConfigurableSessionComparator implements SessionComparator {
    private static final Logger log = LoggerFactory.getLogger(ConfigurableSessionComparator.class);

    private List<FeatureComparator<?>> comparatorChain;
    private DecisionMaker decisionMaker;

    @Override
    @SuppressWarnings("unchecked")
    public SessionVerdict compare(String currentSession, String baselineSession) {

        log.info("Comparing of sessions requested");
        log.info("Feature comparators chain {}", comparatorChain);

        Multimap<String, Verdict> details = ArrayListMultimap.create();
        for (FeatureComparator featureComparator : comparatorChain) {
            String feature = featureComparator.getDescription();

            log.debug("Going to compare feature {}", feature);
            List<Verdict> verdicts = featureComparator.compare(currentSession, baselineSession);
            log.debug("Verdicts for feature {} are {}", feature, verdicts);

            details.putAll(feature, verdicts);
        }

        Decision decision = decisionMaker.makeDecision(details);

        log.info("Sessions compared. Decision {}", decision);

        return new SessionVerdict(decision, details);
    }

    @Required
    public void setComparatorChain(List<FeatureComparator<?>> comparatorChain) {
        this.comparatorChain = comparatorChain;
    }

    @Required
    public void setDecisionMaker(DecisionMaker decisionMaker) {
        this.decisionMaker = decisionMaker;
    }

    public List<FeatureComparator<?>> getComparatorChain() {
        return comparatorChain;
    }

    public DecisionMaker getDecisionMaker() {
        return decisionMaker;
    }
}
