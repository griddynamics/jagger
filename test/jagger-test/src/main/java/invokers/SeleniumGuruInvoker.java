package invokers;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;


/**
 * Implementation of SeleniumInvoker for our case
 */
public class SeleniumGuruInvoker extends SeleniumInvoker<SeleniumQuery, Integer> {

    @Override
    Integer getResponse(String endpoint, SeleniumQuery query, WebDriver driver) {
        return driver.getPageSource().length();
    }

    @Override
    ExpectedCondition getExpectedCondition(String endpoint, SeleniumQuery query, WebDriver driver) {

        SeleniumQuery.HowToLookFor howToLookFor = query.getWayToLookFor();

        switch (howToLookFor) {
            case BY_TAG:
                return ExpectedConditions.presenceOfElementLocated(By.tagName(query.getAttend()));
            default:
                return ExpectedConditions.presenceOfElementLocated(By.cssSelector(query.getAttend()));

        }
    }

    @Override
    String getUrlString(String endpoint, SeleniumQuery query) {
        return endpoint + query.getQuery();
    }
}
