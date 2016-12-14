package metric;

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;
import com.griddynamics.jagger.invoker.http.HttpResponse;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 10/22/13
 * Time: 2:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class ReturnCode implements MetricCalculator<HttpResponse> {
    @Override
    public Number calculate(HttpResponse response) {
        return response.getStatusCode();
    }
}
