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
    private final int popupWidth;
    private final HTML popupPanelContent;

    public ShowCurrentValueHoverListener(PopupPanel popup, int popupWidth, HTML popupPanelContent) {
        this.popup = popup;
        this.popupWidth = popupWidth;
        this.popupPanelContent = popupPanelContent;
    }

    @Override
    public void onPlotHover(Plot plot, PlotPosition position, PlotItem item) {
        if (item != null) {
            popupPanelContent.setHTML("<table width=\"100%\"><tr><td>Time</td><td>" + item.getDataPoint().getX() +
                    "</td></tr><tr><td>Value</td><td>" + item.getDataPoint().getY() + "</td></tr></table>");

            int clientWidth = Window.getClientWidth();
            if (item.getPageX() + popupWidth <= clientWidth) {
                popup.setPopupPosition(item.getPageX() + 10, item.getPageY() - 25);
            } else {
                popup.setPopupPosition(item.getPageX() - popupWidth, item.getPageY() - 25);
            }

            popup.show();
        } else {
            popup.hide();
        }
    }
}
