package metric;

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 9/26/13
 * Time: 12:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class ConstantMetric implements MetricCalculator {

    public Double getConstant() {
        return constant;
    }

    public void setConstant(Double constant) {
        this.constant = constant;
    }

    private Double constant;

    @Override
    public Double calculate(Object response) {
        return constant;
    }
}
