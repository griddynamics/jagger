#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.calculator;

import com.griddynamics.jagger.engine.e1.collector.MetricCalculator;

/* begin: following section is used for docu generation - metric calculator source */
/* Will calculate some parameter from endpoint response
 * @author Grid Dynamics */
public class ResponseSize implements MetricCalculator<String> {

    /* Following method will be called after every successful invoke to evaluate result
     * In this simplified example we will just calculate length of result
     * @author Grid Dynamics
     *
     * @param response - Result returned from endpoint
     * */
    @Override
    public Integer calculate(String response) {
        return response.length();
    }
}
/* end: following section is used for docu generation - metric calculator source */

