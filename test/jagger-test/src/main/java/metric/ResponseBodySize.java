package metric;

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;
import com.griddynamics.jagger.invoker.http.HttpResponse;

public class ResponseBodySize implements MetricCalculator<HttpResponse> {
    @Override
    public Integer calculate(HttpResponse response) {
        return response.getBody().length();
    }
}

