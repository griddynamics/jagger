#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )

package ${package}.listener;

import com.griddynamics.jagger.engine.e1.Provider;
import com.griddynamics.jagger.engine.e1.collector.AvgMetricAggregatorProvider;
import com.griddynamics.jagger.engine.e1.collector.MetricDescription;
import com.griddynamics.jagger.engine.e1.collector.test.TestInfo;
import com.griddynamics.jagger.engine.e1.collector.test.TestListener;
import com.griddynamics.jagger.engine.e1.services.ServicesAware;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* begin: following section is used for docu generation - custom test listener */
public class ProviderOfTestListener extends ServicesAware implements Provider<TestListener> {

    private static final Logger log = LoggerFactory.getLogger(ProviderOfTestListener.class);
    private String commentString = "";

    // Method will be executed single time, when listener provider is initiated
    @Override
    protected void init() {
        // In this example, we will create custom metric to collect internal metrics of SUT
        // Approach is the following:
        // - Declare custom metric
        // - Run test. SUT will usually collect some internal data (metrics)
        // - After test is finished, get all internal data from SUT and save to custom metric
        // In this case internal data will be save to result DB, displayed in test report and Web UI,
        // you can use it in decision maker when running Continues Integration
        MetricDescription metricDescription = new MetricDescription("internalData");
        metricDescription.plotData(false).showSummary(true).addAggregator(new AvgMetricAggregatorProvider());
        getMetricService().createMetric(metricDescription);
    }

    // Method will provide custom test listener to Jagger
    @Override
    public TestListener provide() {

        return new TestListener() {
            // Method will be executed before starting of test
            @Override
            public void onStart(TestInfo testInfo) {
                // We will take no actions in this example
            }

            // Method will be executed after finishing test
            @Override
            public void onStop(TestInfo testInfo) {

                long someMetric = 0;

                // Test is finished and now we are ready to get some internal data from SUT
                // Unfortunately our example is very simple and no data is available
                // So we will just ask what Google knows about Jagger and save number of found results as metric

                // Send request to google
                String url = "https://www.google.com/search?q=griddynamics+jagger";
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet(url);
                try {
                    HttpResponse response = client.execute(request);

                    // Parse response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        if (line.contains("id=\"resultStats\"")) {
                            // How many results google have found
                            Pattern pattern = Pattern.compile("id=\"resultStats\">About(.*?)results</div>");
                            Matcher matcher = pattern.matcher(line);
                            while (matcher.find()) {
                                someMetric = Long.parseLong(matcher.group(1).replaceAll(",","").replace(" ",""));
                            }
                        }
                    }
                }
                catch (Exception e) {}

                // begin: following section is used for docu generation - example of metric saving

                // Save metric
                getMetricService().saveValue("internalData",someMetric);

                // end: following section is used for docu generation - example of metric saving
            }

            // Method will be executed periodically during test run
            @Override
            public void onRun(TestInfo status) {
                // We will take no actions in this example
            }
        };
    }
}
/* end: following section is used for docu generation - custom test listener */