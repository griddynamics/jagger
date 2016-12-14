package com.griddynamics.jagger.webclient.client.handler;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.googlecode.gflot.client.event.PlotClickListener;
import com.googlecode.gflot.client.event.PlotItem;
import com.googlecode.gflot.client.event.PlotPosition;
import com.googlecode.gflot.client.jsni.Plot;
import com.griddynamics.jagger.dbapi.dto.MarkingDto;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.Set;

/**
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/20/12
 */
public class ShowTaskDetailsListener implements PlotClickListener {
    private final Map<String, Set<MarkingDto>> markingsMap;
    private final String plotId;
    private final PopupPanel taskInfoPanel;
    private final HTML taskInfoPanelContent;
    private final int taskInfoPanelWidth;

    public ShowTaskDetailsListener(String plotId, Map<String, Set<MarkingDto>> markingsMap, PopupPanel taskInfoPanel, int taskInfoPanelWidth, HTML taskInfoPanelContent) {
        this.markingsMap = markingsMap;
        this.plotId = plotId;
        this.taskInfoPanel = taskInfoPanel;
        this.taskInfoPanelContent = taskInfoPanelContent;
        this.taskInfoPanelWidth = taskInfoPanelWidth;
    }

    @Override
    public void onPlotClick(Plot plot, PlotPosition position, PlotItem item) {
        if (position == null) {
            return;
        }

        String taskName = "Not Determined";

        double prev = 0;
        Set<MarkingDto> markingDtoSet = markingsMap.get(plotId);
        if (markingDtoSet == null) {
            return;
        }

        for (MarkingDto dto : markingDtoSet) {
            if (position.getX() >= prev && position.getX() <= dto.getValue()) {
                taskName = dto.getTaskName();
                break;
            }
        }

        taskInfoPanelContent.setHTML("<table width=\"100%\"><tr><td>Clicked at</td><td>" +
                new BigDecimal(position.getX()).setScale(2, RoundingMode.HALF_EVEN) + " sec</td></tr>" +
                "<tr><td>Task name</td><td>" + taskName + "</td></tr></table>");

        int clientWidth = Window.getClientWidth();
        if (position.getPageX() + taskInfoPanelWidth < clientWidth) {
            taskInfoPanel.setPopupPosition(position.getPageX() + 10, position.getPageY() - 25);
        } else {
            taskInfoPanel.setPopupPosition(position.getPageX() - taskInfoPanelWidth, position.getPageY() - 25);
        }
        taskInfoPanel.show();
    }
}
