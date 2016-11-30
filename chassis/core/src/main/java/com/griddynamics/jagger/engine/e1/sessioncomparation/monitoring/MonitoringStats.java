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

package com.griddynamics.jagger.engine.e1.sessioncomparation.monitoring;

import com.google.common.base.Objects;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.SortedMap;

@Deprecated
public class MonitoringStats {
    private final SortedMap<Long, Double> points;

    public MonitoringStats(Map<Long, Double> points) {
        this.points = Maps.newTreeMap();

        for (Map.Entry<Long, Double> entry : points.entrySet()) {
            this.points.put(entry.getKey(), entry.getValue());
        }
    }

    public SortedMap<Long, Double> getPoints() {
        return points;
    }

    public double getSttDev() {
        double mean = getMean();

        double sum = 0;
        int count = 0;
        for (Double d : points.values()) {
            sum += (d - mean) * (d - mean);
            count++;
        }

        return Math.sqrt(sum / count);
    }

    public double getMean() {
        double sum = 0;
        int count = 0;

        for (Double d : points.values()) {
            sum += d;
            count++;
        }
        return sum / count;

    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("points", points)
                .add("std dev", getSttDev())
                .add("mean", getMean())
                .toString();
    }
}
