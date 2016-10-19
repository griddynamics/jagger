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

package com.griddynamics.jagger.engine.e1.reporting;

import com.griddynamics.jagger.dbapi.DatabaseService;
import com.griddynamics.jagger.engine.e1.services.DataService;
import com.griddynamics.jagger.engine.e1.services.DefaultDataService;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.reporting.AbstractReportProvider;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.beans.factory.annotation.Required;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class TestDetailsReporter extends AbstractReportProvider {
    
    public static final Comparator<TestDetailsDTO> BY_TEST_NAME = Comparator.comparing(TestDetailsDTO::getTestName);
    
    private DataService dataService;

    @Override
    public JRDataSource getDataSource(String sessionId) {

        Set<TestEntity> testEntities = dataService.getTests(sessionId);

        List<TestDetailsDTO> result = new ArrayList<>();

        for(TestEntity testEntity : testEntities) {
            TestDetailsDTO testDetailsDTO = new TestDetailsDTO();
            testDetailsDTO.setTestName(testEntity.getName());
            testDetailsDTO.setId(testEntity.getId().toString());
            result.add(testDetailsDTO);
        }
    
    
        result.sort(BY_TEST_NAME);
        return new JRBeanCollectionDataSource(result);
    }

    public static class TestDetailsDTO {
        
        private String testName;
        private String id;

        public String getTestName() {
            return testName;
        }

        public void setTestName(String testName) {
            this.testName = testName;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }

    @Required
    public void setDatabaseService(DatabaseService databaseService) {
        this.dataService = new DefaultDataService(databaseService);
    }
}
