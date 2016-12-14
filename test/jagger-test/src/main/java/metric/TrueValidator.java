package metric;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 8/16/13
 * Time: 6:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class TrueValidator extends ResponseValidator {

    public TrueValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TrueValidator";
    }

    @Override
    public boolean validate(Object query, Object endpoint, Object result, long duration) {

        return true;
    }
}
