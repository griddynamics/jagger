#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.listener;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioInfo;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.SessionEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.MetricSummaryValueEntity;
import com.griddynamics.jagger.engine.e1.services.data.service.TestEntity;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.Map;
import java.util.Set;

/* begin: following section is used for docu generation - custom test suite listener */
public class ProviderOfLoadScenarioListener extends ServicesAware implements Provider<LoadScenarioListener> {

    private static final Logger log = LoggerFactory.getLogger(ProviderOfLoadScenarioListener.class);

    // Method will be executed single time, when listener provider is initiated
    @Override
    protected void init() {
        // In this example, we will take version of Jagger artifact from pom file
        // and save this value to Session comment
        // In the same way you can save version(s) of SUT artifacts into session comment
        String POM_JAGGER_VERSION = "jagger.version";
        String pomfile = "../../pom.xml";
        MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();

        try {
            FileReader reader = new FileReader(pomfile);
            Model model = mavenXpp3Reader.read(reader);
            getSessionInfoService().appendToComment("jagger.version: " + model.getProperties().get(POM_JAGGER_VERSION));
        }
        catch(Exception e){
            log.warn("Cant get pom info {}", e);
        }
    }

    // Method will provide custom test suite listener to Jagger
    @Override
    public LoadScenarioListener provide() {

        return new LoadScenarioListener() {
            // Method will be executed before starting of test suite
            @Override
            public void onStart(LoadScenarioInfo loadScenarioInfo) {
                /* begin: following section is used for docu generation - work with session comments */

                // Here you can f.e.:
                // - provide smoke tests to check if your SUT is responding
                // - check system properties required for correct SUT functionality

                // In this example we will append names of nodes participating in test to session comment
                // We will also print OS name and CPU model
                String comment = "";
                for (Map.Entry<NodeId,GeneralNodeInfo> entry : loadScenarioInfo.getGeneralNodeInfo().entrySet()) {
                    comment += "\nNode: " + entry.getKey() + " (OS: " + entry.getValue().getOsName() + ", " + entry.getValue().getCpuModel() + ")";
                }

                getSessionInfoService().appendToComment(comment);

                /* end: following section is used for docu generation - work with session comments */
            }

            // Method will be executed after finishing test suite
            @Override
            public void onStop(LoadScenarioInfo loadScenarioInfo) {
                /* begin: following section is used for docu generation - work with session tags */

                // In this example we will create tags
                getSessionInfoService().saveOrUpdateTag("SERVICE_NAME", "Tag to mark sessions, testing some particular service");
                getSessionInfoService().saveOrUpdateTag("PASS", "Tag to mark sessions with pass results");
                getSessionInfoService().saveOrUpdateTag("FAIL", "Tag to mark sessions with fail results"); 

                // Mark session with some tag from source code
                getSessionInfoService().markSessionWithTag("SERVICE_NAME");

                /* end: following section is used for docu generation - work with session tags */

                /* begin: following section is used for docu generation - access to Jagger results in database */

                // Get information about session
                // Note: session entity for current session is not available (not saved yet) in database at this point of execution,
                // while all detailed results like tests and metrics are already saved to database
                // SessionEntity sessionEntity = getDataService().getSession("1");

                // Get all tests for this session
                Set<TestEntity> testEntities = getDataService().getTests(loadScenarioInfo.getSessionId());

                // Get all metrics for every test
                Map<TestEntity,Set<MetricEntity>> metricsPerTest = getDataService().getMetricsByTests(testEntities);

                // Get summary values for metrics
                for (Map.Entry<TestEntity,Set<MetricEntity>> entry : metricsPerTest.entrySet()) {
                    //System.out.println("\nTest " + entry.getKey().getName() + " from session " + loadScenarioInfo.getSessionId());
                    Map<MetricEntity,MetricSummaryValueEntity> metricValues = getDataService().getMetricSummary(entry.getValue());
                    //System.out.println(String.format("   %-40s   %s","Metric id","Value"));
                    for (Map.Entry<MetricEntity,MetricSummaryValueEntity> valueEntry : metricValues.entrySet()) {
                        //System.out.println(String.format("   %-40s   %s",valueEntry.getKey().getMetricId(),valueEntry.getValue().getValue()));
                    }
                }

                /* end: following section is used for docu generation - access to Jagger results in database */

            }
        };

    }
}
/* end: following section is used for docu generation - custom test suite listener */
