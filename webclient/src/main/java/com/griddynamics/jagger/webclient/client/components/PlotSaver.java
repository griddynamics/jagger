package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.ui.*;
import com.googlecode.gflot.client.Series;
import com.googlecode.gflot.client.SimplePlot;

/**
 * Class that enables to save plots */
public class PlotSaver {

    /**
     * Frame to fire download */
    private Frame hiddenFrame;

    {
        hiddenFrame = new Frame();
        hiddenFrame.setVisible(false);
        hiddenFrame.setPixelSize(0, 0);
        RootPanel.get().add(hiddenFrame);
    }

    /**
     * Saves plot as png
     * @param plot plot to be saved
     * @param plotHeader plot header
     * @param xAxisLabel X axis label
     */
    public void saveAsPng (final SimplePlot plot, final String plotHeader, final String xAxisLabel) {
        if (plot.isExportAsImageEnabled()) {

            Image im = plot.getImage();
            im.addLoadHandler(new LoadHandler() {
                @Override
                public void onLoad(LoadEvent event) {

                    Image imageOfPlot = (Image) event.getSource();
                    drawCanvas(imageOfPlot, plot, plotHeader, xAxisLabel);

                    RootPanel.get().remove(imageOfPlot);
                }
            });

            im.setVisible(false);
            RootPanel.get().add(im);

        } else {
            alert("Can not save image in your browser.");
        }
    }

    private void drawCanvas(Image imageOfPlot, SimplePlot plot, String plotHeader, String xAxisLabel) {
        Canvas canvasTmp = Canvas.createIfSupported();
        if (canvasTmp == null) {
            alert("Can not create canvas element in your browser.");
            return;
        }

        Context2d context = canvasTmp.getContext2d();

        ImageElement imageElement = ImageElement.as(imageOfPlot.getElement());

        // Width of plot image
        int plotWidth = plot.getOffsetWidth();

        // Height of plot image
        int plotHeight = plot.getOffsetHeight();

        // Font size of text in pixels
        int fontSize = 13;

        // Indent size in pixels. Used to separate all elements on final image and defines left indent of header or legend
        int indent = 5;

        JsArray<Series> series = plot.getModel().getSeries();

        // Calculate max width of legend label to avoid overlay
        int maxLegendLabelWidth = calculateLabelMaxWidth(series, context, fontSize);

        int legendRectangleWidth = (int) (fontSize * 1.5);
        int legendRectangleHeight = fontSize;

        // Total width of one series legend including rectangle with color, and indents.
        int oneSeriesLegendSize = legendRectangleWidth + 3 * indent + maxLegendLabelWidth;

        // Number of columns in legend
        int numberOfColumns = plotWidth / oneSeriesLegendSize;

        if (numberOfColumns == 0)
            numberOfColumns = 1;

        int legendHeight = calculateLegendHeight(series, numberOfColumns, fontSize, indent);

        // 2 * fontSize as we have Header of plot and X axis label
        // 3 * indent as we have indent from top to header,
        //                       indent between plot and header,
        //                       indent between plot and X axis label.
        int totalHeight = plotHeight + 2 * fontSize + 3 * indent + legendHeight;


        canvasTmp.setWidth(plotWidth + "px");
        canvasTmp.setHeight(totalHeight + "px");
        canvasTmp.setCoordinateSpaceWidth(plotWidth);
        canvasTmp.setCoordinateSpaceHeight(totalHeight);


        int currentY = 0;
        context.setFillStyle("black");
        context.setFont("bold " + fontSize + "px sans-serif");
        context.setTextAlign(Context2d.TextAlign.START);

        currentY += fontSize + indent;
        // add plot header
        context.fillText(plotHeader, indent, currentY);

        currentY += indent;
        // add image of plot
        context.drawImage(imageElement, 0.0, currentY);//, imageElement.getWidth(), imageElement.getHeight());

        currentY += plotHeight + indent;
        context.setFont(fontSize + "px sans-serif");
        context.setTextAlign(Context2d.TextAlign.CENTER);
        // add x axis label
        context.fillText(xAxisLabel, plotWidth / 2, currentY + fontSize / 2);

        currentY += fontSize + indent;


        int currentX = 0;
        for (int i = 0; i < series.length(); i++) {

            Series s = series.get(i);

            int curRecW = legendRectangleWidth;
            int curRecH = legendRectangleHeight;
            if (i % numberOfColumns == 0) {
                currentX = indent;
            } else {
                currentX += indent + oneSeriesLegendSize;
            }

            // add rectangle with color
            context.setFillStyle("gray");
            context.fillRect(currentX, currentY, curRecW, curRecH);
            context.setFillStyle("white");
            curRecW -= 2;
            curRecH -= 2;
            context.fillRect(currentX + 1, currentY + 1, curRecW, curRecH);
            context.setFillStyle(s.getColor());
            curRecW -= 2;
            curRecH -= 2;
            context.fillRect(currentX + 2, currentY + 2, curRecW, curRecH);
            context.setFillStyle("black");
            context.setTextAlign(Context2d.TextAlign.START);
            // add label of current series
            context.fillText(s.getLabel(), currentX + legendRectangleWidth + indent, currentY + fontSize - 2);

            if (i % numberOfColumns == numberOfColumns - 1) {
                currentY += fontSize + indent;
            }
        }

        // get url of png data created from canvas
        String url = canvasTmp.toDataUrl("image/png");

        // fire browser event to download png
        hiddenFrame.setUrl(url.replace("image/png", "image/octet-stream"));
    }

    private void alert(String message) {
        new ExceptionPanel(message);
    }

    private int calculateLegendHeight(JsArray<Series> series, int numberOfColumns, int fontSize, int delta) {

        int legendHeight = 0;
        for (int i = 0; i < series.length(); i++) {
            if (i % numberOfColumns == 0)
                legendHeight += fontSize + delta;
        }
        return legendHeight;
    }

    private int calculateLabelMaxWidth(JsArray<Series> series, Context2d context, int fontSize) {

        context.setFont(fontSize + "px sans-serif");
        int maxWidth = 0;
        for (int i = 0; i < series.length(); i++) {

            Series s = series.get(i);
            double width = context.measureText(s.getLabel()).getWidth();
            if (maxWidth < width)
                maxWidth = (int)width;
        }

        return maxWidth;
    }
}
