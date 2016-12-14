package com.griddynamics.jagger.webclient.client.components.control;

import com.griddynamics.jagger.webclient.client.components.ControlTree;
import com.griddynamics.jagger.webclient.client.components.SessionComparisonPanel;
import com.griddynamics.jagger.dbapi.model.*;
import com.griddynamics.jagger.webclient.client.handler.*;
import com.griddynamics.jagger.webclient.client.trends.Trends;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides Handlers for specific Node
 * User: amikryukov
 * Date: 11/26/13
 */
public class CheckHandlerMap {

    private static Map<Class, TreeAwareHandler<?>> handlers;

    static {
        handlers = new HashMap<Class, TreeAwareHandler<?>>(){
            {
                put(SummaryNode.class, new SummaryNodeHandler());
                put(SessionInfoNode.class, new SessionInfoNodeHandler());
                put(TestNode.class, new TestNodeHandler());
                put(TestInfoNode.class, new TestInfoNodeHandler());
                put(MetricNode.class, new MetricNodeHandler());
                put(MetricGroupNode.class, new MetricGroupNodeHandler());

                put(DetailsNode.class, new DetailsNodeHandler());
                put(TestDetailsNode.class, new TestDetailsNodeHandler());
                put(PlotNode.class, new PlotNodeHandler());
            }
        };

    }


    public static void setTestInfoFetcher(Trends.TestInfoFetcher testInfoFetcher) {
        for (TreeAwareHandler tah : handlers.values()) {
            tah.setTestInfoFetcher(testInfoFetcher);
        }
    }

    public static void setMetricFetcher(Trends.MetricFetcher metricFetcher) {
        for (TreeAwareHandler tah : handlers.values()) {
            tah.setMetricFetcher(metricFetcher);
        }
    }

    public static void setSessionComparisonPanel(SessionComparisonPanel sessionComparisonPanel) {
        for (TreeAwareHandler tah : handlers.values()) {
            tah.setSessionComparisonPanel(sessionComparisonPanel);
        }
    }

    public static void setTree(ControlTree<String> tree) {
        for (TreeAwareHandler tah : handlers.values()) {
            tah.setTree(tree);
        }
    }

    public static void setTestPlotFetcher(Trends.TestPlotFetcher testPlotFetcher) {
        for (TreeAwareHandler tah : handlers.values()) {
            tah.setTestPlotFetcher(testPlotFetcher);
        }
    }

    public static TreeAwareHandler<?> getHandler(Class clazz) {
        return handlers.get(clazz);
    }
}
