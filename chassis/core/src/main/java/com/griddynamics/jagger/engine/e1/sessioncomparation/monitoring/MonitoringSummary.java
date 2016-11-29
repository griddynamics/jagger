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
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import java.util.Map;
import java.util.Set;

@Deprecated
public class MonitoringSummary {
    private final Table<String, String, MonitoringStats> data;

    public static Builder builder() {
        return new Builder();
    }

    public MonitoringSummary(Table<String, String, MonitoringStats> data) {
        this.data = data;
    }

    public Set<String> getSources() {
        return data.columnKeySet();
    }

    public Set<String> getParams(String source) {
        return data.column(source).keySet();
    }

    public MonitoringStats getStats(String source, String param) {
        return data.get(param, source);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("data", data)
                .toString();
    }

    public static class Builder {
        private final Table<String, String, Map<Long, Double>> data = HashBasedTable.create();

        private Builder() {

        }

        public Builder add(String source, String value, long time, double data) {

            if (!this.data.contains(source, value)) {
                this.data.put(source, value, Maps.<Long, Double>newHashMap());
            }

            this.data.get(source, value).put(time, data);

            return this;
        }

        public MonitoringSummary build() {
            Table<String, String, MonitoringStats> newTable = HashBasedTable.create();

            for (Table.Cell<String, String, Map<Long, Double>> cell : data.cellSet()) {

                MonitoringStats stats = new MonitoringStats(cell.getValue());
                newTable.put(cell.getColumnKey(), cell.getRowKey(), stats);

            }

            return new MonitoringSummary(newTable);
        }
    }
}
