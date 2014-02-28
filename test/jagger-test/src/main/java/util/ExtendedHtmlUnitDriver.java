package util;

import com.gargoylesoftware.htmlunit.SilentCssErrorHandler;
import com.gargoylesoftware.htmlunit.WebClient;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

/**
 * Extension of HtmlUnitDriver to achieve not throwing exceptions on script error
 */
public class ExtendedHtmlUnitDriver extends HtmlUnitDriver {
    @Override
    protected WebClient modifyWebClient(WebClient client) {
        WebClient modifiedClient = super.modifyWebClient(client);

        modifiedClient.setCssErrorHandler(new SilentCssErrorHandler());
        modifiedClient.setThrowExceptionOnScriptError(false);
        return modifiedClient;
    }


    /**
     * Clear cache every time.
     */
    @Override
    public void get(String url) {

        getWebClient().getCache().clear();

        super.get(url);
    }
}
