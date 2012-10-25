package com.griddynamics.jagger.invoker.hessian;


import com.caucho.hessian.client.HessianProxy;
import com.caucho.hessian.client.HessianProxyFactory;
import com.griddynamics.jagger.util.ThreadLocalMetrics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * User: vshulga
 * Date: 10/8/12
 */
public class ResponseAwareHessianProxy extends HessianProxy {
    private final static Logger log = LoggerFactory.getLogger(ResponseAwareHessianProxy.class);

    public ResponseAwareHessianProxy(HessianProxyFactory factory, URL url) {
        super(url, factory);
    }

    @Override
    protected void parseResponseHeaders(URLConnection conn) {
        Map<String, List<String>> headers = conn.getHeaderFields();

        for (Map.Entry<String, List<String>> header : headers.entrySet()) {
            if(header.getKey() != null && header.getKey().startsWith(ThreadLocalMetrics.METRIC_MARKER)) {
                if (header.getValue() == null || header.getValue().isEmpty()) {
                    continue;
                }
                String strValue = header.getValue().get(0);
                Integer value = safeStringToInt(strValue);
                if(value != null) {
                    ThreadLocalMetrics.addMetric(header.getKey(), value);
                } else {
                    log.warn("Header {} is null", header.getKey());
                }
            }
        }
    }

    public static Integer safeStringToInt(String str) {
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
