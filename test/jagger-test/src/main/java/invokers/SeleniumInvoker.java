package invokers;

import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class SeleniumInvoker<Q, R> implements Invoker<Q, R, String>{

    Logger log = LoggerFactory.getLogger(SeleniumInvoker.class);

    private WebDriver driver;
    private long timeoutOfObjectRetrieve;
    private long sleepInMs;

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    public void setTimeoutOfObjectRetrieve(long timeoutOfObjectRetrieve) {
        this.timeoutOfObjectRetrieve = timeoutOfObjectRetrieve;
    }

    public void setSleepInMs(long sleepInMs) {
        this.sleepInMs = sleepInMs;
    }

    @Override
    public R invoke(Q query, String endpoint) throws InvocationException {

        log.debug("invoking with endpoint : {}, query : {}  : ", endpoint, query);
        driver.get(getUrlString(endpoint, query));

        driver.navigate().refresh(); // because of or dummy refresh to show behaviour of our webclient.

        waitForElement(endpoint, query, driver);

        return getResponse(endpoint, query, driver);
    }

    @SuppressWarnings("unchecked")
    private void waitForElement(String endpoint, Q query, WebDriver driver) {

        new WebDriverWait(driver, timeoutOfObjectRetrieve, sleepInMs).
            until(getExpectedCondition(endpoint, query, driver));
    }

    abstract R getResponse(String endpoint, Q query, WebDriver driver);

    abstract ExpectedCondition getExpectedCondition(String endpoint, Q query, WebDriver driver);

    abstract String getUrlString(String endpoint, Q query);
}
