/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.griddynamics.jagger.reporting.chart;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.griddynamics.jagger.util.Pair;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.*;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.renderer.xy.*;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.*;

public class ChartHelper {
    public enum ColorTheme {LIGHT, DARK}

    public static JFreeChart createTimeSeriesChart(String title, List<TimeSeriesChartSpecification> specifications, String xLabel, String yLabel, ColorTheme theme) {

        TimeSeriesCollection dataCollection = new TimeSeriesCollection();
        for (TimeSeriesChartSpecification specification : specifications) {
            TimeSeries series = new TimeSeries(specification.getLabel(), Millisecond.class);
            for (Map.Entry<Date, Double> point : specification.getData().entrySet()) {
                series.add(new Millisecond(point.getKey()), point.getValue());
            }
            dataCollection.addSeries(series);
        }

        JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xLabel, yLabel, dataCollection, true, false, false);

        formatColorTheme(chart, theme);

        XYPlot plot = chart.getXYPlot();

        Color[] colors = generateJetSpectrum(specifications.size());
        for (int i = 0; i < specifications.size(); i++) {
            plot.getRenderer().setSeriesPaint(i, colors[i]);
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("dd/MM hh:mm:ss"));

        return chart;
    }

    public static JFreeChart createXYChart(String title, XYDataset data, String xLabel, String yLabel, int pointRadius, int lineThickness, ColorTheme theme) {
        JFreeChart chart = ChartFactory.createXYLineChart(title, xLabel, yLabel, data, PlotOrientation.VERTICAL, true, false, false);

        formatColorTheme(chart, theme);

        XYPlot plot = (XYPlot) chart.getPlot();
        Shape icon = new Ellipse2D.Double(-pointRadius, -pointRadius, pointRadius * 2, pointRadius * 2);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        plot.setRenderer(renderer);

        Color[] colors = generateJetSpectrum(data.getSeriesCount());
        for (int i = 0; i < data.getSeriesCount(); i++) {
            plot.getRenderer().setSeriesStroke(i, new BasicStroke(lineThickness));
            plot.getRenderer().setSeriesShape(i, icon);
            ((XYLineAndShapeRenderer) plot.getRenderer()).setSeriesShapesVisible(i, true);
            ((XYLineAndShapeRenderer) plot.getRenderer()).setSeriesShapesFilled(i, true);
            plot.getRenderer().setSeriesPaint(i, colors[i]);
        }

        LegendTitle legend = chart.getLegend();
        Font legendFont = legend.getItemFont();
        float legendFontSize = legendFont.getSize();
        Font newLegendFont = legendFont.deriveFont(legendFontSize * 0.6f);
        legend.setItemFont(newLegendFont);

        ValueAxis domainAxis = ((XYPlot) chart.getPlot()).getDomainAxis();
        Font domainAxisLabelFont = domainAxis.getLabelFont();
        float domainAxisLabelFontSize = domainAxisLabelFont.getSize();
        domainAxis.setLabelFont(domainAxisLabelFont.deriveFont(domainAxisLabelFontSize * 0.6f));

        Font domainAxisTickLabelFont = domainAxis.getTickLabelFont();
        float domainAxisTickLabelFontSize = domainAxisTickLabelFont.getSize();
        domainAxis.setTickLabelFont(domainAxisTickLabelFont.deriveFont(domainAxisTickLabelFontSize * 0.6f));

        ValueAxis rangeAxis = ((XYPlot) chart.getPlot()).getRangeAxis();
        Font rangeAxisLabelFont = rangeAxis.getLabelFont();
        float rangeAxisLabelFontSize = rangeAxisLabelFont.getSize();
        rangeAxis.setLabelFont(rangeAxisLabelFont.deriveFont(rangeAxisLabelFontSize * 0.6f));

        Font rangeAxisTickLabelFont = rangeAxis.getTickLabelFont();
        float rangeAxisTickLabelFontSize = rangeAxisTickLabelFont.getSize();
        rangeAxis.setTickLabelFont(rangeAxisTickLabelFont.deriveFont(rangeAxisTickLabelFontSize * 0.6f));

        return chart;
    }

    public static JFreeChart createStackedBarChart(String title, List<List<Double>> bars, List<String> zoneLabels, String xLabel, String yLabel, ColorTheme theme) {
        DefaultCategoryDataset ds = new DefaultCategoryDataset();

        Integer barId = 0;
        int maxStack = 0;
        for (List<Double> bar : bars) {
            int i = 0;
            maxStack = Math.max(maxStack, bar.size());
            for (double value : bar) {
                ds.addValue(value, zoneLabels.get(i), barId);
                i++;
            }
            barId++;
        }

        JFreeChart chart = ChartFactory.createStackedBarChart(title, xLabel, yLabel, ds, PlotOrientation.VERTICAL, true, false, false);

        formatColorTheme(chart, theme);
        formatBars(chart);

        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.getDomainAxis().setCategoryMargin(0);
        plot.getDomainAxis().setLowerMargin(0);
        plot.getDomainAxis().setUpperMargin(0);

        Color[] colors = generateJetSpectrum(maxStack);
        for (int i = 0; i < maxStack; i++) {
            plot.getRenderer().setSeriesPaint(i, colors[i]);
            plot.getRenderer().setSeriesOutlinePaint(i, Color.white);
        }

        return chart;
    }

    public static JFreeChart createStackedAreaChart(String title, XYSeriesCollection areaData, XYSeriesCollection lineData, String xLabel, String yLabel, ColorTheme theme) {

        final ValueAxis xAxis = new NumberAxis(xLabel);
        final ValueAxis yAxis = new NumberAxis(yLabel);
        XYPlot mainPlot = new XYPlot();
        mainPlot.setDomainAxis(xAxis);
        mainPlot.setRangeAxis(yAxis);

        mainPlot.setForegroundAlpha(0.9f);

        //[ stacked area
        DefaultTableXYDataset areaDs = new DefaultTableXYDataset();
        for (int i = 0; i < areaData.getSeriesCount(); i++) {
            areaDs.addSeries(areaData.getSeries(i));
        }
        XYItemRenderer stackedRenderer = new StackedXYAreaRenderer2();
        mainPlot.setDataset(areaDs);
        mainPlot.setRenderer(stackedRenderer);

        Color[] colors = generateJetSpectrum(areaData.getSeriesCount());
        for (int i = 0; i < areaData.getSeriesCount(); i++) {
            stackedRenderer.setSeriesPaint(i, colors[i]);
        }
        //]

        //[ lines
        if (lineData != null) {
            XYItemRenderer lineRenderer = new StandardXYItemRenderer();
            DefaultTableXYDataset lineDs = new DefaultTableXYDataset();
            for (int i = 0; i < lineData.getSeriesCount(); i++) {
                lineDs.addSeries(lineData.getSeries(i));
            }
            mainPlot.setDataset(1, lineDs);
            mainPlot.setRenderer(1, lineRenderer);

            colors = new Color[]{Color.black, Color.red, Color.darkGray};
            for (int i = 0; i < lineData.getSeriesCount(); i++) {
                lineRenderer.setSeriesPaint(i, colors[i % colors.length]);
                lineRenderer.setSeriesStroke(i, new BasicStroke(2f));
            }
        }
        //]

        mainPlot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        JFreeChart chart = new JFreeChart(title, JFreeChart.DEFAULT_TITLE_FONT, mainPlot, true);

        formatColorTheme(chart, theme);

        return chart;
    }

    public static Pair<String, XYSeriesCollection> adjustTime(XYSeriesCollection chartsCollection, Collection<IntervalMarker> markers) {
        int maxTime = 0;
        for (int i = 0; i < chartsCollection.getSeriesCount(); i++) {
            XYSeries series = chartsCollection.getSeries(i);
            for (int j = 0; j < series.getItemCount(); j++) {
                int x = series.getX(j).intValue();
                if (x > maxTime) {
                    maxTime = x;
                }
            }
        }

        String type = "ms";
        int div = 1;

        if (maxTime > 10 * 60 * 1000) {
            div = 60 * 1000;
            type = "min";
        }

        if (maxTime > 30 * 1000) {
            div = 1000;
            type = "sec";
        }

        XYSeriesCollection result = new XYSeriesCollection();

        for (int i = 0; i < chartsCollection.getSeriesCount(); i++) {

            XYSeries old = chartsCollection.getSeries(i);
            XYSeries series = new XYSeries(old.getKey(), old.getAutoSort(), old.getAllowDuplicateXValues());
            for (int j = 0; j < old.getItemCount(); j++) {
                Number x = old.getX(j).doubleValue() / div;
                Number y = old.getY(j);
                series.add(x, y);
            }

            result.addSeries(series);
        }

        if (markers != null) {
            for (IntervalMarker marker : markers) {
                marker.setStartValue(marker.getStartValue() / div);
                marker.setEndValue(marker.getEndValue() / div);
            }
        }

        return Pair.of(type, result);
    }


    private static void formatColorTheme(JFreeChart chart, ColorTheme theme) {
        Plot rawPlot = chart.getPlot();

        Paint background = Color.BLACK;
        Paint foregroung = Color.WHITE;
        if (theme == ColorTheme.LIGHT) {
            background = Color.WHITE;
            foregroung = Color.BLACK;
        }

        chart.setBackgroundPaint(background);
        chart.getLegend().setBackgroundPaint(background);
        chart.getLegend().setItemPaint(foregroung);

        if (rawPlot instanceof XYPlot) {
            XYPlot plot = (XYPlot) rawPlot;
            plot.getDomainAxis().setLabelPaint(foregroung);
            plot.getRangeAxis().setLabelPaint(foregroung);
            plot.getDomainAxis().setTickLabelPaint(foregroung);
            plot.getDomainAxis().setTickMarkPaint(foregroung);
            plot.getRangeAxis().setTickLabelPaint(foregroung);
            plot.getRangeAxis().setTickMarkPaint(foregroung);
            plot.setBackgroundPaint(background);
            plot.setDomainGridlinePaint(foregroung);
            plot.setRangeGridlinePaint(foregroung);
        }
        if (rawPlot instanceof CategoryPlot) {
            CategoryPlot plot = (CategoryPlot) rawPlot;
            plot.getDomainAxis().setLabelPaint(foregroung);
            plot.getRangeAxis().setLabelPaint(foregroung);
            plot.getDomainAxis().setTickLabelPaint(foregroung);
            plot.getDomainAxis().setTickMarkPaint(foregroung);
            plot.getRangeAxis().setTickLabelPaint(foregroung);
            plot.getRangeAxis().setTickMarkPaint(foregroung);
            plot.setBackgroundPaint(background);
            plot.setDomainGridlinePaint(foregroung);
            plot.setRangeGridlinePaint(foregroung);
        }
    }

    private static void formatBars(JFreeChart chart) {
        if (chart != null) {
            Plot p = chart.getPlot();
            if (p instanceof CategoryPlot) {
                CategoryPlot cp = (CategoryPlot) p;
                CategoryItemRenderer cir = cp.getRenderer();
                if (cir instanceof BarRenderer) {
                    BarRenderer br = (BarRenderer) cir;
                    br.setShadowVisible(false);
                    br.setBarPainter(new StandardBarPainter());
                    br.setDrawBarOutline(true);
                }
            }
        }
    }

    private static Color[] generateJetSpectrum(int n) {
        Color[] colors = new Color[n];
        for (int i = 0; i < n; i++) {
            colors[i] = Color.getHSBColor(-(0.75f * i / n + 0.3f), 0.85f, 1.0f);
        }
        return colors;
    }


    public static class TimeSeriesChartSpecification {
        private Map<Date, Double> data;
        private String label;

        public Map<Date, Double> getData() {
            return data;
        }

        public void setData(Map<Date, Double> data) {
            this.data = data;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

}
