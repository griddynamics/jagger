package ${package};

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;

public class Metric_ResponseSize implements MetricCalculator<String> {
    @Override
    public Integer calculate(String response) {
        return response.length();
    }
}

