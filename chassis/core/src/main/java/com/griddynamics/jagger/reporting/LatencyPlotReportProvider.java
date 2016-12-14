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

package com.griddynamics.jagger.reporting;

import com.griddynamics.jagger.reporting.chart.ChartHelper;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.renderers.JCommonDrawableRenderer;
import org.jfree.chart.JFreeChart;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LatencyPlotReportProvider extends AbstractReportProvider {

    public class PlotDTO {
        private JCommonDrawableRenderer image;

        public JCommonDrawableRenderer getImage() {
            return image;
        }

        public void setImage(JCommonDrawableRenderer image) {
            this.image = image;
        }
    }

    public JRDataSource getDataSource(String sessionID) {

        List<PlotDTO> plots = new ArrayList<PlotDTO>();

        for (int i = 0; i < 5; i++) {
            JFreeChart chart = createChart("Node [" + i + "]");

            PlotDTO dto = new PlotDTO();
            dto.setImage(new JCommonDrawableRenderer(chart));
            plots.add(dto);
        }

        return new JRBeanCollectionDataSource(plots);
    }

    private JFreeChart createChart(String title) {

        if(false) {
            Map<Date, Double> s1 = new HashMap<Date, Double>();
            Map<Date, Double> s2 = new HashMap<Date, Double>();
            long time = System.currentTimeMillis();
            for(int i=0; i<100; i++) {
                s1.put(new Date(time + i*1000), Math.sin(i/10.0));
                s2.put(new Date(time + i*1000), Math.cos(i/5.0));
            }
            ChartHelper.TimeSeriesChartSpecification spec1 = new ChartHelper.TimeSeriesChartSpecification();
            spec1.setData(s1);
            spec1.setLabel("Latency 1");
            ChartHelper.TimeSeriesChartSpecification spec2 = new ChartHelper.TimeSeriesChartSpecification();
            spec2.setData(s2);
            spec2.setLabel("Latency 2");

            return ChartHelper.createTimeSeriesChart(title, Arrays.asList(spec1, spec2), "Time", "Latency", ChartHelper.ColorTheme.LIGHT);
        }

        if(false) {

            List<List<Double>> data = getBarData();
            List<String> zoneLabels = new ArrayList<String>();
            for(int i = 0; i < 16; i++) {
                zoneLabels.add(i + "%");
            }
            JFreeChart chart = ChartHelper.createStackedBarChart(title, data, zoneLabels, "Time", "Latency", ChartHelper.ColorTheme.DARK);

            return  chart;
        }

        if(true) {
            XYSeriesCollection areas = getSeriesCollection(16);
            XYSeriesCollection lines = getSeriesCollection(2);
            JFreeChart chart = ChartHelper.createStackedAreaChart(title, areas, lines, "Time", "Latency", ChartHelper.ColorTheme.LIGHT);

            return chart;
        }

        return null;
    }

    public static List<List<Double>> getBarData() {
        Random rnd = new Random();
        List<List<Double>> range = new ArrayList<List<Double>>();
        for(int i = 0; i < 64; i++) {
            List<Double> stripe = new ArrayList<Double>();
            for(int j = 0; j < 16; j++) {
                stripe.add((double)rnd.nextInt(50)+1);
            }
            range.add(stripe);
        }

        return range;
    }

    public static XYSeriesCollection getSeriesCollection(int seriesCount) {
        XYSeriesCollection result = new XYSeriesCollection();
        Random rnd = new Random();
        for(int i=0; i<seriesCount; i++) {
            XYSeries series = new XYSeries(i, false, false);
            result.addSeries(series);
            for(int t=0; t<100; t++) {
                series.add(t, rnd.nextInt(50));
            }
        }

        return result;
    }

}