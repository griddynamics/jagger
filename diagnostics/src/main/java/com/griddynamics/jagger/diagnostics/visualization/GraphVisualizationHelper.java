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

package com.griddynamics.jagger.diagnostics.visualization;

import edu.uci.ics.jung.algorithms.layout.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ChainedTransformer;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Map;

public class GraphVisualizationHelper {
    public enum GraphLayout {CIRCLE, ISOM, FR, KK}
    public enum ColorTheme {LIGHT, DARK}

    private static final double IMAGE_HORIZONTAL_MARGIN = 0.2;
    private static final double IMAGE_VERTICAL_MARGIN = 0.2;
    private static final int MAX_LABEL_LENGTH = 30;

    public static <V, E> Image renderGraph(Graph<V, E> graph, int width, int height, GraphLayout graphLayout, final ColorTheme colorTheme, final Map<V, Paint> customNodeColors) {

        Layout<V, E> layout;
        switch (graphLayout) {
            case CIRCLE : layout = new CircleLayout<V, E>(graph); break;
            case ISOM : layout = new ISOMLayout<V, E>(graph); break;
            case FR : layout = new FRLayout<V, E>(graph); break;
            case KK : layout = new KKLayout<V, E>(graph); break;
            default : throw new RuntimeException("Unknown Graph Layout : [" + graphLayout + "]");
        }

        layout.setSize(new Dimension((int)(width * (1 - IMAGE_HORIZONTAL_MARGIN)), (int)(height * (1 - IMAGE_VERTICAL_MARGIN))));

        VisualizationImageServer<V, E> server = new VisualizationImageServer<V, E>(
                displacementLayout(layout, (int)(width * IMAGE_HORIZONTAL_MARGIN/2), (int)(height * IMAGE_VERTICAL_MARGIN/2)),
                new Dimension(width, height));

        final Color edgeColor;
        switch (colorTheme) {
            case LIGHT : server.setBackground(Color.WHITE); edgeColor = Color.BLACK; break;
            case DARK : server.setBackground(Color.BLACK); edgeColor = Color.LIGHT_GRAY; break;
            default : throw new RuntimeException("Unknown Color Theme : [" + colorTheme + "]");
        }

        Transformer<V, Paint> vertexPaint = new Transformer<V, Paint>() {
            public Paint transform(V v) {
                Paint paint = customNodeColors.get(v);
                if(paint == null) {
                    paint = Color.LIGHT_GRAY;
                }
                return paint;
            }
        };

        Transformer<V, Paint> vertexBorderPaint = new Transformer<V, Paint>() {
            public Paint transform(V v) {
                return Color.DARK_GRAY;
            }
        };

        Transformer<E, Paint> edgePaint = new Transformer<E, Paint>() {
            public Paint transform(E e) {
                return edgeColor;
            }
        };

        server.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        server.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
        server.getRenderContext().setArrowDrawPaintTransformer(edgePaint);
        server.getRenderContext().setArrowFillPaintTransformer(edgePaint);

        server.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<V>());
        server.getRenderContext().setEdgeLabelTransformer(new ToStringLabeller<E>());
        server.getRenderContext().setVertexDrawPaintTransformer(vertexBorderPaint);

        server.getRenderContext().setVertexLabelTransformer(
                new ChainedTransformer<V, String>(
                        new Transformer[]{
                                new ToStringLabeller<V>(),
                                new Transformer<String, String>() {
                                    public String transform(String input) {
                                        return "<html><center><p>" + formatLabel(input, MAX_LABEL_LENGTH);
                                    }
                                }}));
        VertexLabelAsShapeRenderer<V, E> vlasr = new VertexLabelAsShapeRenderer<V, E>(server.getRenderContext());
        server.getRenderContext().setVertexShapeTransformer(vlasr);
        server.getRenderer().getVertexLabelRenderer().setPosition(Renderer.VertexLabel.Position.CNTR);

        return server.getImage(new Point(0, 0), new Dimension(width, height));
    }

    private static String formatLabel(String string, int maxLength) {
        StringBuffer buffer = new StringBuffer(string);
        int runLength = 0;
        for(int i = 0; i < buffer.length(); i++) {
            if(buffer.charAt(i) != '\n') {
                runLength++;
            } else {
                runLength = 0;
            }

            if(runLength >= maxLength) {
                buffer.insert(i, '\n');
                runLength = 0;
            }
        }

        return buffer.toString().replaceAll("\\n", "<br/>");
    }

    private static <V, E> Layout<V, E> displacementLayout(Layout<V, E> layout, final int xOffset, final int yOffset) {
        return new LayoutDecorator<V, E>(layout) {
            @Override
            public Point2D transform(V v) {
                Point2D point = delegate.transform(v);
                return new Point.Double(point.getX() + xOffset, point.getY() + yOffset);
            }
        };
    }
}
