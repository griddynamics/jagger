package metric;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.invoker.v2.JHttpEndpoint;
import com.griddynamics.jagger.invoker.v2.JHttpQuery;
import com.griddynamics.jagger.invoker.v2.JHttpResponse;

/**
 * User: kgribov
 * Date: 8/16/13
 * Time: 6:26 PM
 */
public class TrueValidator extends ResponseValidator<JHttpQuery, JHttpEndpoint, JHttpResponse> {

    public TrueValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "TrueValidator";
    }
    
    @Override
    public boolean validate(JHttpQuery query, JHttpEndpoint endpoint, JHttpResponse result, long duration) {
        return true;
    }
}
