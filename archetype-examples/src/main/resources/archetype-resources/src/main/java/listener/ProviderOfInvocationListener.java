#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.listener;

import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.SumMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationInfo;
import com.griddynamics.jagger.engine.e1.collector.invocation.InvocationListener;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioInfo;
import com.griddynamics.jagger.engine.e1.collector.loadscenario.LoadScenarioListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.http.HttpResponse;
import com.griddynamics.jagger.util.GeneralNodeInfo;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/* begin: following section is used for docu generation - custom invocation listener */
public class ProviderOfInvocationListener extends ServicesAware implements Provider<InvocationListener> {

    private static final Logger log = LoggerFactory.getLogger(ProviderOfInvocationListener.class);
    private List<Integer> codes = new ArrayList<Integer>();

    // Method will be executed single time, when listener provider is initiated
    @Override
    protected void init() {
        // We will take no actions in this example
    }

    // Method will provide custom test suite listener to Jagger
    @Override
    public InvocationListener provide() {
        return new InvocationListener() {

            // In this example we will create listener that will check what
            // Http codes are returned by SUT and calculate how often every code is returned
            // As result you will get following info in report:
            // - number of invocations per each code
            // - number of invocations per each code vs time

            // Method will be executed before invoke
            @Override
            public void onStart(InvocationInfo invocationInfo) {
                // We will take no actions in this example
            }

            // Method will be executed after successful invoke
            @Override
            public void onSuccess(InvocationInfo invocationInfo) {
                saveResult(invocationInfo);
            }

            // Method will be executed when some invocation exception happens or some validator failed
            @Override
            public void onFail(InvocationInfo invocationInfo, InvocationException e) {
                saveResult(invocationInfo);
            }

            // Method is executed when invocation was interrupted by some error
            @Override
            public void onError(InvocationInfo invocationInfo, Throwable error) {
                saveResult(invocationInfo);
            }

            private void saveResult(InvocationInfo invocationInfo) {
                // if failed with exception => result will be null
                // if failed by validator => result will contain error code and we will save it
                if (invocationInfo.getResult() != null) {
                    HttpResponse myResult = (HttpResponse) invocationInfo.getResult();
                    int code = myResult.getStatusCode();
                    String metricId = "http_return_code_" + code;

                    // IMPORTANT: saveResult method will be executed during every invocation = every request to SUT
                    // Try to avoid slow operations in invocation listener code. They will slow down your workload
                    // Creating new metric is time consuming opreration. We are creating metrics dynamically in this example,
                    // because we don't know beforehand what HTTP codes will be returned => what metric ids will be used.
                    // As soon as you know metric ids in advance, create your metrics in init() method above!

                    // Create new metric if it doesn't exist
                    if (!codes.contains(code)) {
                        codes.add(code);

                        MetricDescription metricDescription = new MetricDescription(metricId);
                        metricDescription.plotData(true).showSummary(true).displayName("Http return code " + code + " ").addAggregator(new SumMetricAggregatorProvider());
                        getMetricService().createMetric(metricDescription);
                    }

                    // Save metric. After every invoke we will add 1 to some code bin
                    // During aggregation we will just summarize these value to get info
                    // how many invokes per code were executed
                    getMetricService().saveValue(metricId,1);
                }
            }
        };
    }
}
/* end: following section is used for docu generation - custom invocation listener */
