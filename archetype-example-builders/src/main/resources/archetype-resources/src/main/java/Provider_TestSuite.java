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

package com.gd;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.griddynamics.jagger.user.test.configurations.JTest;
import com.griddynamics.jagger.user.test.configurations.JTestDescription;
import com.griddynamics.jagger.user.test.configurations.JTestGroup;
import com.griddynamics.jagger.user.test.configurations.JTestSuite;
import com.griddynamics.jagger.user.test.configurations.load.JLoad;
import com.griddynamics.jagger.user.test.configurations.load.JLoadRps;
import com.griddynamics.jagger.user.test.configurations.termination.JTermination;
import com.griddynamics.jagger.user.test.configurations.termination.JTerminationIterations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

public class Provider_TestSuite {

    public static List<JTestSuite> userConfigurations() {
        JTestDescription description = JTestDescription.builder()
                .withComment("My comment")
                .withId("FIRst")
                .withEndpointsProvider(Collections.singletonList("https://jagger.griddynamics.net"))
                .withQueryProvider(Arrays.asList("index.html", "screenshots.html"))
                .withInvokerClass(Invoker_PageVisitor.class)
                .build();

        JLoad load = JLoadRps.builder()
                .withMaxLoadThreads(12)
                .withRequestPerSecond(24)
                .withWarmUpTimeInSeconds(42)
                .build();

        JTermination termination = JTerminationIterations.builder()
                .withIterationsCount(21)
                .withMaxDurationInSeconds(42)
                .build();

        JTest test1 = JTest.builder()
                .withJTestDescription(description)
                .withLoad(load)
                .withTermination(termination)
                .withId("42")
                .build();


        JTestGroup testGroup = JTestGroup.builder()
                .withId("ID2")
                .withTests(Collections.singletonList(test1))
                .build();

        JTestSuite configuration = JTestSuite.builder()
                .withTestGroups(Collections.singletonList(testGroup))
                .build();

        return Collections.singletonList(configuration);
    }
}