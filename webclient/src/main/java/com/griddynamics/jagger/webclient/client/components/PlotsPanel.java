package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ScrollEvent;
import com.google.gwt.event.dom.client.ScrollHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SeriesData;
import com.googlecode.gflot.client.SimplePlot;
import com.googlecode.gflot.client.Zoom;
import com.griddynamics.jagger.dbapi.model.MetricNode;
import com.sencha.gxt.widget.core.client.tree.Tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Class that hold widgets of type PlotContainer with dynamic layout feature.
 */
public class PlotsPanel extends ResizeComposite {

    interface PlotsPanelUiBinder extends UiBinder<Widget, PlotsPanel> {
    }

    private static PlotsPanelUiBinder ourUiBinder = GWT.create(PlotsPanelUiBinder.class);

    @UiField
    /* Main layout panel where all children will be */
    protected DynamicLayoutPanel<PlotContainer> layoutPanel;

    @UiField
    /* Menu bar to control plot panel */
    protected PlotButtonsPanel plotButtonsPanel;

    @UiField
    /* Scroll bar for layout panel */
    protected ScrollPanel scrollPanelMetrics;

    private ControlTree<String> controlTree;

    public PlotsPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));


        plotButtonsPanel.setupButtonPanel(this);
        layoutPanel.setChildHeight(plotButtonsPanel.getPlotHeight());
    }

    public void setControlTree(ControlTree<String> controlTree) {
        this.controlTree = controlTree;
    }

    /**
     * Deselect metric node in control tree. This will lead to plot removal from plot panel
     * @param metricNode metric node */
    public void deselectMetricNode(MetricNode metricNode) {
        controlTree.setChecked(metricNode, Tree.CheckState.UNCHECKED);
    }

    /**
     * Remove widgets from layout panel by metric node
     * @param metricNodes metric nodes */
     public void removeByMetricNodes(Collection<MetricNode> metricNodes) {

        Set<String> ids = new HashSet<String>();
        for (MetricNode metricNode : metricNodes) {
            ids.add(metricNode.getId());
        }
        layoutPanel.removeChildren(ids);
        childrenCount = layoutPanel.getAllChildren().size();
        setMaxRange();
    }

    /**
     * Remove all widgets from layoutPanel */
    public void clear() {
        layoutPanel.clear();
        childrenCount = 0;
    }

    /**
     * Add widget to layoutPanel
     * @param plotContainer child widget */
    public void addElement(final PlotContainer plotContainer) {
        plotContainer.setHeight(plotButtonsPanel.getPlotHeight() + "px");
        plotContainer.setPlotsPanel(this);
        scrollCalculations(plotContainer);

        plotContainer.getPlotRepresentation().addAttachHandler(new AttachEvent.Handler() {
            @Override
            public void onAttachOrDetach(AttachEvent event) {
                // executes when plot have been loaded
                plotContainer.getPlotRepresentation().calculateScrollWidth();
                avalancheScrollEventsCount ++;
                plotContainer.getPlotRepresentation().panToPercent(percent);
            }
        });

        layoutPanel.setAdditionalHeightForChild(
                plotContainer.getDragPanelHeight()
                + plotContainer.getPlotRepresentation().getZoomPanelHeight()
                + plotContainer.getPlotRepresentation().getScrollPanelHeight()
                + plotContainer.getPlotRepresentation().getXAxisLabelHeight()
        );
        layoutPanel.addChild(plotContainer);
        childrenCount = layoutPanel.getAllChildren().size();
        setMaxRange();
    }

    private void panAllPlots(double percent) {

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            pc.getPlotRepresentation().panToPercent(percent);
        }
    }


    private void scrollCalculations(final PlotContainer plotContainer) {

        double maxX = calculateMaxXAxisValue(plotContainer.getPlotRepresentation().getSimplePlot());

        double maxRange;
        if (this.isEmpty()) {
            maxRange = maxX;
        } else {
            if (maxX > getMaxXAxisValue()) {
                maxRange = maxX;
            } else {
                maxRange = getMaxXAxisValue();
            }
        }

        plotContainer.getPlotRepresentation().setMaxRange(maxRange);

        final NativeHorizontalScrollbar scrollBar = plotContainer.getPlotRepresentation().getScrollbar();
        scrollBar.setVisible(true);
        scrollBar.addScrollHandler(new ScrollHandler() {
            @Override
            public void onScroll(ScrollEvent event) {

                if (avalancheScrollEventsCount > 0) {
                    avalancheScrollEventsCount --;
                    return;
                }

                avalancheScrollEventsCount = childrenCount;
                int currentPosition = scrollBar.getHorizontalScrollPosition();
                double percent = 1D * (currentPosition - scrollBar.getMinimumHorizontalScrollPosition()) /
                        (scrollBar.getMaximumHorizontalScrollPosition() - scrollBar.getMinimumHorizontalScrollPosition());
                PlotsPanel.this.percent = percent;
                panAllPlots(percent);
            }
        });

    }

    /**
     * Global counter to see how many avalanche scroll events hav not been finished */
    private int avalancheScrollEventsCount = 0;

    /**
     * To avoid calculating on every scroll  event */
    private int childrenCount = 0;

    /**
     * Current state of plot`s scrolls */
    private double percent = 0;

    private void setMaxRange() {
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            pc.getPlotRepresentation().setMaxRange(getMaxXAxisValue());
        }
    }

    /**
     * Check if PlotsPanel contains element with certain id
     * @param plotId id of element to identify
     * @return true if element found with given plotId, false otherwise */
    public boolean containsElementWithId(String plotId) {
        return layoutPanel.containsElementWithId(plotId);
    }


    /**
     * Zoom all plots in PlotsPanel */
    public void zoomIn() {

       zoom(false);
    }

    /**
     * Zoom out all plots in PlotsPanel */
    public void zoomOut() {

       zoom(true);
    }

    /**
     * Zoom in if param is false, zoom out otherwise.
     * @param out defines whether zoom in or out.
     */
    private void zoom(boolean out) {

        double maxRange = layoutPanel.getFirstChild().getPlotRepresentation().getMaxRange();
        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot plot = pc.getPlotRepresentation().getSimplePlot();
            Zoom zoom = Zoom.create().setAmount(1.1);

            if (out) {
                plot.zoomOut(zoom);
            } else {
                plot.zoom(zoom);
            }

            pc.getPlotRepresentation().calculateScrollWidth();
        }

        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        double percent;
        double minVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMinimumValue();
        double maxVisible = plotRepresentation.getSimplePlot().getAxes().getX().getMaximumValue();

        if (out) {
            if (maxVisible >= maxRange && minVisible <= 0) {
                // do nothing when plot in visible range
                return;
            }

            if (maxVisible >= maxRange) {
                // to the end
                plotRepresentation.panToPercent(1);
                return;
            } else if (minVisible <= 0) {
                // to very start
                plotRepresentation.panToPercent(0);
                return;
            }
        }

        percent = minVisible / (maxRange - maxVisible + minVisible);
        plotRepresentation.panToPercent(percent);
    }

    /**
     * Zoom to size of given plot;
     * @param plot - given plot */
    public void zoomDefault(SimplePlot plot) {

        double xMaxValue = calculateMaxXAxisValue(plot);

        for (PlotContainer pc : layoutPanel.getAllChildren()) {
            SimplePlot currentPlot = pc.getPlotRepresentation().getSimplePlot();
            // currently we always start xAxis with zero
            currentPlot.getOptions().getXAxisOptions().setMinimum(0).setMaximum(xMaxValue);
            currentPlot.setupGrid();
            currentPlot.redraw();
            pc.getPlotRepresentation().calculateScrollWidth();
        }

        // all plots start with zero
        PlotRepresentation plotRepresentation = layoutPanel.getFirstChild().getPlotRepresentation();
        plotRepresentation.panToPercent(0);
    }


    /**
     * Returns max X axis value on plot
     * @param plot plot
     * @return max X axis value */
    private double calculateMaxXAxisValue (SimplePlot plot) {

        JsArray<Series> seriesArray = plot.getModel().getSeries();
        double maxValue = Double.MIN_VALUE;
        for (int i = 0; i < seriesArray.length(); i ++) {
            // get curve
            SeriesData curve = seriesArray.get(i).getData();
            double temp = curve.getX(curve.length() - 1);
            if (maxValue < temp) {
                maxValue = temp;
            }
        }
        return maxValue;
    }

    /**
     * Check if PlotsPanel contains any plots.
     * @return true if it is empty, false otherwise */
    public boolean isEmpty() {
        return childrenCount == 0;
    }


    /**
     * @return maximum X axis value */
    public double getMaxXAxisValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        double xMaxValue = Double.MIN_VALUE;
        for (PlotContainer pc : layoutPanel.getAllChildren()) {

            double curveMaxX = calculateMaxXAxisValue(pc.getPlotRepresentation().getSimplePlot());

            if (curveMaxX > xMaxValue) {
                xMaxValue = curveMaxX;
            }
        }

        return xMaxValue;
    }

    /**
     * @return maximum visible X axis value */
    public double getMaxXAxisVisibleValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();

        return plot.getAxes().getX().getMaximumValue();
    }

    /**
     * @return minimum visible X axis value */
    public double getMinXAxisVisibleValue() {
        // no widgets in panel
        assert layoutPanel.getWidgetCount() > 0;

        SimplePlot plot = layoutPanel.getFirstChild().getPlotRepresentation().getSimplePlot();
        return plot.getAxes().getX().getMinimumValue();
    }

    /**
     * Change layout of plots (single columns, two columns) */
    public void changeLayout() {
        layoutPanel.changeLayout(layoutPanel.getLayout().getNext());
    }

    /**
     * Change height of plots */
    public void changeChildrenHeight(Integer height) {
        layoutPanel.changeChildrenHeight(height);
    }

    /**
     * Scroll to bottom of layout panel (panel with plots) */
    public void scrollToBottom() {
        scrollPanelMetrics.scrollToBottom();
    }
}
