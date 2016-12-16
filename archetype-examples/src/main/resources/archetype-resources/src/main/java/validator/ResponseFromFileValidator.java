#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.validator;

import com.griddynamics.jagger.coordinator.NodeContext;
import com.griddynamics.jagger.engine.e1.collector.ResponseValidator;
import com.griddynamics.jagger.exception.TechnicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Scanner;

/* Will compare result of invokation with expected result, read from file
 * @author Grid Dynamics
 *
 * @param <Q> - Query type
 * @param <E> - Endpoint type
 * @param <R> - Result type
 */
public class ResponseFromFileValidator<Q, E, R> extends ResponseValidator<Q, E, R> {

    private static final Logger log = LoggerFactory.getLogger(ResponseFromFileValidator.class);

    private String filePath= "suite/validator/resources/response.txt";
    private String expectedResponse;

    public ResponseFromFileValidator(String taskId, String sessionId, NodeContext kernelContext) {
        super(taskId, sessionId, kernelContext);
    }

    @Override
    public String getName() {
        return "ResponseFromFileValidator";
    }

    private void initiate(){
        StringBuilder sb = new StringBuilder();
        String fs = System.getProperty("line.separator");
        Scanner scanner=null;
        try {
            scanner= new Scanner(new FileInputStream(filePath));
            while (scanner.hasNextLine()){
                sb.append(scanner.nextLine() + fs);
            }
        } catch (FileNotFoundException e) {
            throw new TechnicalException("Error during read file ", e);
        } finally{
            if(scanner!=null){
                scanner.close();
            }
        }
        expectedResponse=sb.toString();
    }

    /* Following method will be called after every successful invoke to validate result
     * @author Grid Dynamics
     *
     * @param query    - Query that was sent to endpoint
     * @param endpoint - Endpoint - service under test
     * @param result   - Result returned from endpoint
     * @param duration - Duration of invoke
     * */
    @Override
    public boolean validate(Q query, E endpoint, R result, long duration) {
        if(expectedResponse==null){
            synchronized (filePath){
                if(expectedResponse==null){
                    initiate();
                }
            }
        }
        if (expectedResponse.equals(result)) {
            return true;
        }
        else {
            log.warn("Validator {} failed",getName());
            return false;
        }
    }
}
