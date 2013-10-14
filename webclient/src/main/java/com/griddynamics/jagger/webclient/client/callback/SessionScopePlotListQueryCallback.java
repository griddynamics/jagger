package com.griddynamics.jagger.webclient.client.callback;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Panel;
import com.griddynamics.jagger.webclient.client.PlotsServingBase;
import com.griddynamics.jagger.webclient.client.components.ExceptionPanel;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/22/12
 */
public class SessionScopePlotListQueryCallback extends PlotsServingBase implements AsyncCallback<Set<String>> {

    private String sessionId;
    private Panel sessionScopePlotList;
    private HTMLPanel plotPanel;
    private ClickHandler sessionScopePlotCheckBoxClickHandler;
    private final JaggerResources resources;

    public SessionScopePlotListQueryCallback(String sessionId, Panel sessionScopePlotList, HTMLPanel plotPanel, ClickHandler sessionScopePlotCheckBoxClickHandler, JaggerResources resources) {
        this.sessionId = sessionId;
        this.sessionScopePlotList = sessionScopePlotList;
        this.plotPanel = plotPanel;
        this.sessionScopePlotCheckBoxClickHandler = sessionScopePlotCheckBoxClickHandler;
        this.resources = resources;
    }

    @Override
    public void onFailure(Throwable caught) {
        new ExceptionPanel("Error is occurred during server request processing (Session scope plot names for task fetching)");
    }

    @Override
    public void onSuccess(Set<String> result) {
        // Populate session scope available plots
        sessionScopePlotList.clear();
        for (String plotName : result) {
            SafeHtmlBuilder sb = new SafeHtmlBuilder();
            sb.appendHtmlConstant("<table><tr><td>");
            sb.append(AbstractImagePrototype.create(resources.getPlotImage()).getSafeHtml());
            sb.appendHtmlConstant("</td><td>");
            sb.appendEscaped(plotName);
            sb.appendHtmlConstant("</td></tr></table>");
            CheckBox checkBox = new CheckBox(sb.toSafeHtml());

            // If plot for this one is already rendered we check it
            if (plotPanel.getElementById(generateSessionScopePlotId(sessionId, plotName)) != null) {
                checkBox.setValue(true, false);
            }
            checkBox.getElement().setId(generateSessionScopePlotId(sessionId, plotName) + "_checkbox");
            checkBox.addClickHandler(sessionScopePlotCheckBoxClickHandler);
            sessionScopePlotList.add(checkBox);
        }
    }
}
