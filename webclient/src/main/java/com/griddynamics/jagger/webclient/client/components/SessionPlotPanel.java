package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.*;
import com.google.gwt.view.client.MultiSelectionModel;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

import java.util.HashMap;
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
    final private MultiSelectionModel<String> selectionModel;
    private HTMLPanel plotPanel;
    private final ValueChangeHandler<Boolean> clickHandler = new ValueChangeHandler<Boolean>() {

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            String plotName = ((CheckBox)event.getSource()).getText();
            Boolean value = event.getValue();
            selectionModel.setSelected(plotName, value);
        }
    };

    public SessionPlotPanel(HTMLPanel plotPanel){
        this.plotPanel = plotPanel;
        selectionModel = new MultiSelectionModel<String>();
    }

    public MultiSelectionModel<String> getSelectionModel() {
        return selectionModel;
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
                selectionModel.setSelected(plotName, true);
                checkBox.setValue(true, false);
            }
            checkBox.getElement().setId(generateSessionScopePlotId(sessionId, plotName) + "_checkbox");
            checkBox.addValueChangeHandler(clickHandler);
            add(checkBox);
        }
    }

    public void setSelected(String plot){
        if (map.containsKey(plot)){
            selectionModel.setSelected(plot, true);
            map.get(plot).setValue(true);
        }
    }

    public void setSelected(Set<String> plots){
        for (String plot : plots){
            setSelected(plot);
        }
    }

    public Set<String> getSelected(){
        return selectionModel.getSelectedSet();
    }

    public void clearPlots(){
        clear();
        selectionModel.clear();
        map = new HashMap<String, CheckBox>();
    }

    protected String generateSessionScopePlotId(String sessionId, String plotName) {
        return sessionId + "#session-scope-plot-" + plotName.toLowerCase().replaceAll("\\s+", "-");
    }
}
