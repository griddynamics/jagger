#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.listener;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteInfo;
import com.griddynamics.jagger.engine.e1.collector.testsuite.TestSuiteListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.Map;

/* begin: following section is used for docu generation - custom test suite listener */
public class ProviderOfTestSuiteListener extends ServicesAware implements Provider<TestSuiteListener> {

    private static final Logger log = LoggerFactory.getLogger(ProviderOfTestSuiteListener.class);

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
    public TestSuiteListener provide() {

        return new TestSuiteListener() {
            // Method will be executed before starting of test suite
            @Override
            public void onStart(TestSuiteInfo testSuiteInfo) {
                // Here you can f.e.:
                // - provide smoke tests to check if your SUT is responding
                // - check system properties required for correct SUT functionality

                // In this example we will append names of nodes participating in test to session comment
                // We will also print OS name and CPU model
                String comment = "";
                for (Map.Entry<NodeId,GeneralNodeInfo> entry : testSuiteInfo.getGeneralNodeInfo().entrySet()) {
                    comment += "\nNode: " + entry.getKey() + " (OS: " + entry.getValue().getOsName() + ", " + entry.getValue().getCpuModel() + ")";
                }

                getSessionInfoService().appendToComment(comment);
            }

            // Method will be executed after finishing test suite
            @Override
            public void onStop(TestSuiteInfo testSuiteInfo) {
                // We will take no actions in this example
            }
        };

    }
}
/* end: following section is used for docu generation - custom test suite listener */
