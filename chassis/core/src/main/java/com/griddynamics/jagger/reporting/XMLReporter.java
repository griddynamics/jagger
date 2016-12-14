package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.dbapi.entity.SessionData;
import com.griddynamics.jagger.engine.e1.reporting.OverallSessionComparisonReporter;
import com.griddynamics.jagger.engine.e1.reporting.SessionStatusReporter;
import com.griddynamics.jagger.engine.e1.sessioncomparation.SessionVerdict;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * User: nmusienko
 * Date: 01.02.13
 * Time: 19:14
 */
public class XMLReporter {

    private static final String COMPARISON_REPORT_FILE_NAME = "result.xml";
    private static final String JAGGER_TAG_NAME = "jagger";

    private static final String COMPARISON_TAG_NAME = "comparison";
    private static final String DECISION_TAG_NAME = "decision";
    private static final String BASELINE_TAG_NAME = "baseline";
    private static final String CURRENT_TAG_NAME = "current";

    private static final String SESSION_SUMMARY_TAG_NAME = "summary";
    private static final String TASKS_EXECUTED_TAG_NAME = "executedTasks";
    private static final String TASKS_FAILED_TAG_NAME = "failedTasks";
    private static final String SESSION_STATUS_TAG_NAME = "sessionStatus";


    private static final String SESSION_SUMMARY = "sessionSummary";
    private static final String SESSION_STATUS = "sessionStatus";


    private ReportingContext context;
    private String sessionId;

    private static final Logger log = LoggerFactory.getLogger(XMLReporter.class);

    /**
     * create SessionStatusXMLMaker with context
     * @param context - context
     * @return SessionStatusXMLMaker with context
     */
    public static XMLReporter create(ReportingContext context, String sessionId) {
        XMLReporter maker = new XMLReporter();
        maker.setContext(context);
        maker.sessionId = sessionId;
        return maker;
    }

    /**
     * generate XML report
     */
    public void generateReport() {
        try {
            log.info("BEGIN: Export XML report");

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();
            doc.appendChild(doc.createElement(JAGGER_TAG_NAME));

            fillSessionStatus(doc);
            fillSessionSummary(doc);
            fillComparisonResult(doc);

            Source source = new DOMSource(doc);
            Result result = new StreamResult(new File(COMPARISON_REPORT_FILE_NAME));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);

            log.info("END: Export XML report");
        } catch (Exception e) {
            log.error("Error during XML report generation", e);
        }
    }

    /**
     * creates comparison nodes in document
     * @param doc - document
     * @throws ParserConfigurationException
     */
    private void fillComparisonResult(Document doc) throws ParserConfigurationException {
        if (context.getParameters().containsKey(OverallSessionComparisonReporter.JAGGER_VERDICT)) {
            Element rootElement=doc.getDocumentElement();
            Element report= doc.createElement(COMPARISON_TAG_NAME);
            rootElement.appendChild(report);

            Element decisionElement = doc.createElement(DECISION_TAG_NAME);
            SessionVerdict sessionVerdict = (SessionVerdict) context.getParameters().get(OverallSessionComparisonReporter.JAGGER_VERDICT);
            decisionElement.setTextContent(sessionVerdict.getDecision().toString());
            report.appendChild(decisionElement);

            Element baselineElement = doc.createElement(BASELINE_TAG_NAME);
            String baseline = (String) context.getParameters().get(OverallSessionComparisonReporter.JAGGER_SESSION_BASELINE);
            baselineElement.setTextContent(baseline);
            report.appendChild(baselineElement);

            Element currentElement = doc.createElement(CURRENT_TAG_NAME);
            String current = (String) context.getParameters().get(OverallSessionComparisonReporter.JAGGER_SESSION_CURRENT);
            currentElement.setTextContent(current);
            report.appendChild(currentElement);
        }  else{
            log.info("Session comparison is not available. Skipping");
        }
    }

    /**
     * creates session summary nodes in document
     * @param doc - document
     * @throws ParserConfigurationException
     */
    private void fillSessionSummary(Document doc) throws ParserConfigurationException {
        Element rootElement=doc.getDocumentElement();
        if(context.getProvider(SESSION_SUMMARY)!=null){
            Element summary;
            if(rootElement.getElementsByTagName(SESSION_SUMMARY_TAG_NAME).getLength()>0){
                summary=(Element) rootElement.getElementsByTagName(SESSION_SUMMARY_TAG_NAME).item(0);
            } else {
                summary=doc.createElement(SESSION_SUMMARY_TAG_NAME);
                rootElement.appendChild(summary);
            }

            JRBeanCollectionDataSource source=(JRBeanCollectionDataSource)
                    context.getProvider(SESSION_SUMMARY).getDataSource(sessionId);
            if(source.getData().size()==1){
                SessionData data= (SessionData) source.getData().iterator().next();

                Element executedElement = doc.createElement(TASKS_EXECUTED_TAG_NAME);
                executedElement.setTextContent(data.getTaskExecuted().toString());
                summary.appendChild(executedElement);

                Element failedElement = doc.createElement(TASKS_FAILED_TAG_NAME);
                failedElement.setTextContent(data.getTaskFailed().toString());
                summary.appendChild(failedElement);
            } else {
                log.info("Session summary size is {}, but expected 1. Skipping", source.getData().size());
            }
        }
    }

    /**
     * creates session status node in document
     * @param doc - document
     * @throws ParserConfigurationException
     */
    private void fillSessionStatus(Document doc) throws ParserConfigurationException {
        Element rootElement=doc.getDocumentElement();
        if(context.getProvider(SESSION_STATUS)!=null){
            JRBeanCollectionDataSource source=(JRBeanCollectionDataSource) context.getProvider(SESSION_STATUS).getDataSource(sessionId);
            if(source.getData().size()==1){

                Element summary=doc.createElement(SESSION_SUMMARY_TAG_NAME);
                rootElement.appendChild(summary);

                SessionStatusReporter.SessionStatus data = (SessionStatusReporter.SessionStatus) source.getData().iterator().next();

                Element statusElement = doc.createElement(SESSION_STATUS_TAG_NAME);
                statusElement.setTextContent(data.getDecision().toString());
                summary.appendChild(statusElement);
            } else {
                log.info("Session status size is {}, but expected 1. Skipping",source.getData().size());
            }
        } else {
            log.info("Session status is not available. Skipping");
        }
    }

    public ReportingContext getContext() {
        return context;
    }

    public void setContext(ReportingContext context) {
        this.context = context;
    }
    
    public String getSessionId() {
        return sessionId;
    }
}
