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

package com.griddynamics.jagger.monitoring;

import com.google.common.base.Objects;
import com.griddynamics.jagger.agent.model.MonitoringParameter;
import com.griddynamics.jagger.agent.model.MonitoringParameterLevel;
import org.hibernate.annotations.Index;

import javax.persistence.Embeddable;

@Embeddable
public class MonitoringParameterBean implements MonitoringParameter {
    @Index(name="description_index")
    private String description;

    private MonitoringParameterLevel level;

    private boolean cumulativeCounter;

    public static MonitoringParameterBean copyOf(MonitoringParameter parameter) {
        MonitoringParameterBean result = new MonitoringParameterBean();
        result.setDescription(parameter.getDescription());
        result.setCumulativeCounter(parameter.isCumulativeCounter());
        result.setLevel(parameter.getLevel());
        return result;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public MonitoringParameterLevel getLevel() {
        return level;
    }

    public void setLevel(MonitoringParameterLevel level) {
        this.level = level;
    }

    @Override
    public boolean isCumulativeCounter() {
        return cumulativeCounter;
    }

    public void setCumulativeCounter(boolean cumulativeCounter) {
        this.cumulativeCounter = cumulativeCounter;
    }

    @Override
    public String getId() {
        // in current implementation there is no way to set id for custom jmx metrics
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MonitoringParameterBean that = (MonitoringParameterBean) o;

        if (description != null ? !description.equals(that.description) : that.description != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("description", description)
                .add("level", level)
                .add("cumulativeCounter", cumulativeCounter)
                .toString();
    }
}