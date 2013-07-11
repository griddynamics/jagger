package com.griddynamics.jagger.webclient.client.handler;

import ca.nanometrics.gflot.client.event.PlotHoverListener;
import ca.nanometrics.gflot.client.event.PlotItem;
import ca.nanometrics.gflot.client.event.PlotPosition;
import ca.nanometrics.gflot.client.jsni.Plot;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class ShowCurrentValueHoverListener implements PlotHoverListener {
    private final PopupPanel popup;
    private final HTML popupPanelContent;

    public ShowCurrentValueHoverListener(PopupPanel popup, HTML popupPanelContent) {
        this.popup = popup;
        this.popupPanelContent = popupPanelContent;
    }

    @Override
    public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
        if (item != null) {
            String label = item.getSeries().getLabel();
            popupPanelContent.setHTML("<table width=\"100%\"><tr><td>Plot</td><td>"+label+"</td></tr>" +
                    "<tr><td>Time</td><td>" + item.getDataPoint().getX() +
                    "</td></tr><tr><td>Value</td><td>" + item.getDataPoint().getY() + "</td></tr></table>");

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
