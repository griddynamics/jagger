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

package com.griddynamics.jagger.agent.model;

import com.google.common.collect.Lists;
import com.griddynamics.jagger.dbapi.parameter.MonitoringParameterImpl;
import com.griddynamics.jagger.dbapi.parameter.MonitoringParameterLevel;

import javax.management.ObjectName;
import java.util.ArrayList;

/**
 * @author Nikolay Musienko
 *         Date: 15.07.13
 */

public class JmxMetricGroup {

    public static final char ATTRIBUTE_DELIMETER = ':';

    private String groupName;
    private ObjectName objectName;
    private String[] attributes;

    private ArrayList<JmxMetric> metrics = null;

    public ArrayList<JmxMetric> getJmxMetrics() {
        if (metrics != null) {
            return metrics;
        }
        metrics = Lists.newArrayListWithExpectedSize(attributes.length);

        for (int i = 0; i < attributes.length; ++i) {
            metrics.add(new JmxMetric(
                    new MonitoringParameterImpl(objectName.getCanonicalName() + ATTRIBUTE_DELIMETER + attributes[i], MonitoringParameterLevel.SUT, false),
                    objectName,
                    attributes[i]));
        }
        return metrics;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public String[] getAttributes() {
        return attributes;
    }

    public void setAttributes(String[] attributes) {
        this.attributes = attributes;
    }
}
