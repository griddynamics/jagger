package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 5/30/13
 * Time: 3:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class SessionPlotPanel extends VerticalPanel {

    private HashMap<String, CheckBox> map = new HashMap<String, CheckBox>();
    private ValueChangeHandler<Boolean> clickHandler;
    private HTMLPanel plotPanel;

    public SessionPlotPanel(ValueChangeHandler<Boolean> valueChangeHandler, HTMLPanel plotPanel){
        this.clickHandler = valueChangeHandler;
        this.plotPanel = plotPanel;
    }

    public void update(String sessionId, Set<String> plots){
        clear();
        map = new HashMap<String, CheckBox>();
        for (String plotName : plots) {
            SafeHtmlBuilder sb = new SafeHtmlBuilder();
            sb.appendHtmlConstant("<table><tr><td>");
            sb.append(AbstractImagePrototype.create(JaggerResources.INSTANCE.getPlotImage()).getSafeHtml());
            sb.appendHtmlConstant("</td><td>");
            sb.appendEscaped(plotName);
            sb.appendHtmlConstant("</td></tr></table>");
            CheckBox checkBox = new CheckBox(sb.toSafeHtml());
            map.put(plotName, checkBox);
            // If plot for this one is already rendered we check it
            if (plotPanel.getElementById(generateSessionScopePlotId(sessionId, plotName)) != null) {
                checkBox.setValue(true, false);
            }
            checkBox.getElement().setId(generateSessionScopePlotId(sessionId, plotName) + "_checkbox");
            checkBox.addValueChangeHandler(clickHandler);
            add(checkBox);
        }
    }

    public void setSelected(String plot){
        if (map.containsKey(plot)){
            map.get(plot).setValue(true, true);
        }
    }

    public void setSelected(Set<String> plots){
        for (String plot : plots){
            setSelected(plot);
        }
    }

    public Set<String> getSelected(){
        Set<String> set = new HashSet<String>();
        for (String plotName : map.keySet()){
            CheckBox box = map.get(plotName);
            if (box.getValue()){
                set.add(plotName);
            }
        }
        return set;
    }

    public void clearPlots(){
        clear();
        map = new HashMap<String, CheckBox>();
    }

    protected String generateSessionScopePlotId(String sessionId, String plotName) {
        return sessionId + "#session-scope-plot-" + plotName.toLowerCase().replaceAll("\\s+", "-");
    }
}
