package com.griddynamics.jagger.webclient.client.handler;

import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.googlecode.gflot.client.event.PlotHoverListener;
import com.googlecode.gflot.client.event.PlotItem;
import com.googlecode.gflot.client.event.PlotPosition;
import com.googlecode.gflot.client.jsni.Plot;

import java.util.List;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class ShowCurrentValueHoverListener implements PlotHoverListener {
    private final PopupPanel popup;
    private final HTML popupPanelContent;
    private final String xAxisLabel;
    private final List<Integer> chosenSessions;

    public ShowCurrentValueHoverListener(PopupPanel popup, HTML popupPanelContent, String xAxisLabel, List<Integer> chosenSessions) {
        this.popup = popup;
        this.popupPanelContent = popupPanelContent;
        this.xAxisLabel = xAxisLabel;
        this.chosenSessions = chosenSessions;
    }

    @Override
    public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
        if (item != null) {
            String label = item.getSeries().getLabel();
            double xAxis = item.getDataPoint().getX();

            popupPanelContent.setHTML("<table width=\"100%\"><tr><td>Plot</td><td>"+label+"</td></tr>" +
                    "<tr><td>" + xAxisLabel + "</td><td>" +
                    ((chosenSessions != null) ? chosenSessions.get((int)xAxis) : xAxis ) +
                    "</td></tr><tr><td>Value</td><td>" + NumberFormat.getFormat("0.0###").format(item.getDataPoint().getY()) + "</td></tr></table>");

            int clientWidth = Window.getClientWidth();

            int popupWidth = 8*(5+label.length());
            popup.setWidth(popupWidth+"px");

            if (item.getPageX() + popupWidth + 10 <= clientWidth) {
                popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
            } else {
                popup.setPopupPosition(item.getPageX() - popupWidth - 10, item.getPageY() - 25);
            }

            popup.show();
        } else {
            popup.hide();
        }
    }
}
