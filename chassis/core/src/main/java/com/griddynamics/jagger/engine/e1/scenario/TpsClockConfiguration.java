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

import com.griddynamics.jagger.util.JavaSystemClock;
import com.griddynamics.jagger.util.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

public class TpsClockConfiguration implements WorkloadClockConfiguration {
    private static final Logger log = LoggerFactory.getLogger(TpsClockConfiguration.class);

    private int tickInterval;
    private double tps;
    private MaxTpsCalculator maxTpsCalculator = new DefaultMaxTpsCalculator();
    private WorkloadSuggestionMaker workloadSuggestionMaker;
    private int maxThreadNumber = 50;
    private int maxThreadDiff = 10;
    private SystemClock systemClock = new JavaSystemClock();

    public void setTickInterval(int tickInterval) {
        this.tickInterval = tickInterval;
    }

    @Override
    public WorkloadClock getClock() {
        log.debug("Going to create workload clock");
        TpsRouter tpsRouter = new DefaultTpsRouter(new ConstantTps(new BigDecimal(tps)), maxTpsCalculator, systemClock);

        if (workloadSuggestionMaker == null) {
            workloadSuggestionMaker = new DefaultWorkloadSuggestionMaker(maxThreadDiff);
        }

        return new TpsClock(tickInterval, tpsRouter, workloadSuggestionMaker, systemClock, maxThreadNumber);
    }

    public int getTickInterval() {
        return tickInterval;
    }

    public void setValue(double tps) {
        this.tps = tps;
    }

    public double getTps() {
        return tps;
    }

    public void setTps(double tps) {
        this.tps = tps;
    }

    public MaxTpsCalculator getMaxTpsCalculator() {
        return maxTpsCalculator;
    }

    public void setMaxTpsCalculator(MaxTpsCalculator maxTpsCalculator) {
        this.maxTpsCalculator = maxTpsCalculator;
    }

    public void setMaxThreadNumber(int maxThreadNumber) {
        this.maxThreadNumber = maxThreadNumber;
    }

    public void setMaxThreadDiff(int maxThreadDiff) {
        this.maxThreadDiff = maxThreadDiff;
    }

    public SystemClock getSystemClock() {
        return systemClock;
    }

    public void setSystemClock(SystemClock systemClock) {
        this.systemClock = systemClock;
    }

    @Override
    public String toString() {
        return tps + " tps";
    }

    public void setWorkloadSuggestionMaker(WorkloadSuggestionMaker workloadSuggestionMaker) {
        this.workloadSuggestionMaker = workloadSuggestionMaker;
    }
}
