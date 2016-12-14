package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gflot.client.Pan;
import com.googlecode.gflot.client.SimplePlot;
import com.griddynamics.jagger.dbapi.dto.PlotIntegratedDto;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

/**
 * class that represents plot with all its functionality */
public class PlotRepresentation extends LayoutPanel {

    private FlowPanel zoomPanel;
    private SimplePlot simplePlot;
    private Label xLabel;
    private MetricNode metricNode;

    private MyScroll scrollbar;

    // model of plot
    private PlotIntegratedDto plotIntegratedDto;

    private LegendTree legendTree;
    private LegendState legendState = LegendState.DEFAULT;

    private final int TOTAL_BORDERS_WIDTH = 4;
    private final int DEFAULT_LEGEND_PANEL_WIDTH = 200;
    private final int LEGEND_PANEL_WIDTH;

    private final int ZOOM_PANEL_HEIGHT = 20;
    private final int SCROLL_PANEL_HEIGHT = 20;
    private final int X_AXIS_LABEL_HEIGHT = 20;

    private Label legendLabel;

    public int getXAxisLabelHeight() {
        return X_AXIS_LABEL_HEIGHT;
    }

    public int getScrollPanelHeight() {
        return SCROLL_PANEL_HEIGHT;
    }

    public int getZoomPanelHeight() {
        return ZOOM_PANEL_HEIGHT;
    }

    /**
     * Range to scroll to */
    private double maxRange = 1;

    public PlotRepresentation(
            MetricNode metricNode,
            FlowPanel zoomPanel,
            SimplePlot simplePlot,
            LegendTree legendTree,
            String xLabelString,
            PlotIntegratedDto plotIntegratedDto) {
        super();
        this.setWidth("100%");
        this.zoomPanel = zoomPanel;
        this.zoomPanel.setHeight(ZOOM_PANEL_HEIGHT + "px");

        this.plotIntegratedDto = plotIntegratedDto;

        legendLabel = new Label(legendState.getMessage());
        legendLabel.setStyleName(JaggerResources.INSTANCE.css().zoomLabel());
        legendLabel.addClickHandler(new LegendLabelClickHandler());

        // fixed size of show/hide legend label
        legendLabel.setWidth("100px");

        zoomPanel.add(legendLabel);
        this.simplePlot = simplePlot;
        this.metricNode = metricNode;

        this.xLabel = new Label(xLabelString);
        this.xLabel.addStyleName(JaggerResources.INSTANCE.css().xAxisLabel());
        this.xLabel.setHeight(X_AXIS_LABEL_HEIGHT + "px");

        this.legendTree = legendTree;

        // determine legend tree width - trick
        AbsolutePanel ap = new AbsolutePanel();
        ap.setSize("100%", "100%");
        ap.add(legendTree);
        ap.setWidgetPosition(legendTree, 0, 0);
        RootLayoutPanel.get().add(ap);
        LEGEND_PANEL_WIDTH = legendTree.getOffsetWidth();
        RootLayoutPanel.get().remove(ap);


        simplePlot.setWidth("100%");

        final VerticalPanel vp = new VerticalPanel();
        vp.setWidth("100%");
        vp.add(zoomPanel);
        vp.add(simplePlot);

        HorizontalPanel hp = new HorizontalPanel();
        hp.setWidth("100%");
        hp.setHeight(SCROLL_PANEL_HEIGHT + "px");

        scrollbar = new MyScroll();
        scrollbar.setWidth("100%");
        scrollbar.setVisible(true);
        scrollbar.setHorizontalScrollPosition(0);

        // simple div
        SimplePanel sp = new SimplePanel();
        sp.setVisible(false);
        hp.add(sp);
        hp.setCellWidth(sp, simplePlot.getOptions().getYAxisOptions().getLabelWidth().intValue() + "px");
        hp.add(scrollbar);

        vp.add(hp);
        vp.add(xLabel);

        this.add(vp);
        legendTree.setStyleName(JaggerResources.INSTANCE.css().legendPanel());
        this.add(legendTree);
    }

    public double getMaxRange() {
        return maxRange;
    }

    public void calculateScrollWidth() {
        if (scrollbar.isAttached()) {
            double plotWidth = getPlotWidth() - TOTAL_BORDERS_WIDTH;
            double visibleRange = getVisibleRange();
            double ratio = maxRange / visibleRange;
            scrollbar.setScrollWidth((int) (plotWidth * ratio));
        }
    }

    public void setMaxRange(double maxRange) {
        this.maxRange = maxRange;
        calculateScrollWidth();
    }

    public MyScroll getScrollbar() {
        return scrollbar;
    }

    public FlowPanel getZoomPanel() {
        return zoomPanel;
    }

    public SimplePlot getSimplePlot() {
        return simplePlot;
    }

    public Label getxLabel() {
        return xLabel;
    }

    public PlotIntegratedDto getPlotIntegratedDto() {
        return plotIntegratedDto;
    }


    public LegendTree getLegendTree() {
        return legendTree;
    }


    public MetricNode getMetricNode() {
        return metricNode;
    }

    public void panToPercent(double percent) {

        double minVisible = simplePlot.getAxes().getX().getMinimumValue();
        double visibleRange = getVisibleRange();

        double valueOnScaleShouldBe = percent * (maxRange - visibleRange);

        double deltaInScale = valueOnScaleShouldBe - minVisible;
        double pixelsToScale = getPlotWidth() / visibleRange;

        Pan pan = Pan.create().setLeft(deltaInScale * pixelsToScale).setPreventEvent(true);
        simplePlot.pan(pan);

        int newHorizontalPosition = (int) ((scrollbar.getMaximumHorizontalScrollPosition() - scrollbar.getMinimumHorizontalScrollPosition()) * percent);
        if (newHorizontalPosition == scrollbar.getHorizontalScrollPosition()) {
            // fire scroll event anyway
            NativeEvent event = Document.get().createScrollEvent();
            DomEvent.fireNativeEvent(event, scrollbar);
        } else {
            scrollbar.setHorizontalScrollPosition(newHorizontalPosition);
        }
    }

    private double getPlotWidth() {
        return simplePlot.getOffsetWidth() - simplePlot.getOptions().getYAxisOptions().getLabelWidth();
    }

    private double getVisibleRange() {
        return simplePlot.getAxes().getX().getMaximumValue() - simplePlot.getAxes().getX().getMinimumValue();
    }

    @Override
    public void onResize() {

        calculateScrollWidth();
        calculateLegendPosition();
    }


    /**
     * Put Legend into the right place on PlotRepresentation panel */
    private void calculateLegendPosition() {

        if ( LegendState.HIDE == legendState ) {
            return;
        }

        final int BORDERS = 4;
        final int DELTA = 3;
        final int ZOOM_PANEL_WIDTH = zoomPanel.getOffsetHeight();

        int legendWidth = (LegendState.DEFAULT == legendState) ? DEFAULT_LEGEND_PANEL_WIDTH : LEGEND_PANEL_WIDTH;

        legendWidth += 20; // scroll width
        int legendHeight = calculateLegendHeight();

        this.setWidgetRightWidth(legendTree, BORDERS, Style.Unit.PX, legendWidth, Style.Unit.PX);
        this.setWidgetTopHeight(legendTree, ZOOM_PANEL_WIDTH + BORDERS + DELTA, Style.Unit.PX, legendHeight, Style.Unit.PX);
    }

    /**
     * Calculate height for legend
     * @return legend's height
     */
    private int calculateLegendHeight() {
        // height of all plot minus axis labels, borders height.
        return simplePlot.getOffsetHeight() - X_AXIS_LABEL_HEIGHT - 2 * TOTAL_BORDERS_WIDTH;
    }


    /**
     * Click handler for legend label
     */
    private class LegendLabelClickHandler implements ClickHandler {

        @Override
        public void onClick(ClickEvent event) {

            // get next state
            legendState = legendState.getNext();

            legendLabel.setText(legendState.getMessage());

            switch (legendState) {
                case HIDE:
                    PlotRepresentation.this.setWidgetVisible(legendTree, false);
                    break;
                default:
                    PlotRepresentation.this.setWidgetVisible(legendTree, true);
                    calculateLegendPosition();
            }
        }
    }

    /**
     * State of the legend with title of legend label
     */
    private enum LegendState {
        DEFAULT("Full size legend"), FULL_TEXT("Hide legend"), HIDE("Show legend");

        private String message;

        private LegendState(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        LegendState getNext() {return LegendState.values()[(this.ordinal() + 1) % LegendState.values().length];}
    }
}
