package ${package};

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;

public class ResponseSize implements MetricCalculator<String> {
    @Override
    public Integer calculate(String response) {
        return response.length();
    }
}

