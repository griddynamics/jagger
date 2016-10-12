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

package com.griddynamics.jagger.reporting;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.griddynamics.jagger.engine.e1.reporting.OverallSessionComparisonReporter;
import com.griddynamics.jagger.util.Decision;
import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionVerdict;
import com.griddynamics.jagger.engine.e1.sessioncomparation.Verdict;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;
import java.net.URL;


public class ReportingServiceTest {

    @BeforeTest
    public void cleanUp(){
        File f=new File("result.xml");
        if(f.exists()){
            f.delete();
        }
    }

    @Test(enabled = false)
    public void testReportingService() throws IOException, SAXException {
        ApplicationContext context = new ClassPathXmlApplicationContext("spring/test-reporting.xml");
        ReportingService reportingService = (ReportingService) context.getBean("reportingService");
        reportingService.renderReport(true);
        checkXML();
    }

    @Test(enabled = true)
    public void testReportingServiceXMLValidate() throws SAXException, IOException {
        Multimap<String, Verdict> details = ArrayListMultimap.create();
        SessionVerdict sessionVerdict=new SessionVerdict(Decision.OK, details);
        ReportingContext reportingContext=new ReportingContext();
        reportingContext.getParameters().put(OverallSessionComparisonReporter.JAGGER_VERDICT,sessionVerdict);
        reportingContext.getParameters().put(OverallSessionComparisonReporter.JAGGER_SESSION_BASELINE,"11");
        reportingContext.getParameters().put(OverallSessionComparisonReporter.JAGGER_SESSION_CURRENT,"11");
        XMLReporter maker= XMLReporter.create(reportingContext);
        maker.generateReport();
        checkXML();
    }

    @Test(enabled = true)
    public void testReportingServiceXMLEmptyContext() throws IOException, SAXException {
        ReportingContext reportingContext=new ReportingContext();
        XMLReporter maker= XMLReporter.create(reportingContext);
        maker.generateReport();
        assertTrue(new File("result.xml").exists());
        checkXML();
    }

    public void checkXML() throws SAXException, IOException {
        Source xmlFile = new StreamSource(new File("result.xml"));
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

        URL reportUrl = getClass().getResource("/reporting/test-xml-report.xsd");
        Schema schema = schemaFactory.newSchema(reportUrl);
        Validator validator = schema.newValidator();
        validator.validate(xmlFile);
        assertTrue(new File("result.xml").delete());
    }
}
