package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

/**
 * Class that holds menu bar for PlotPanel
 */
public class PlotButtonsPanel extends HorizontalPanel {

    private PlotsPanel plotsPanel;

    private TextButton changeLayout = new TextButton("Change layout");
    private Slider heightSlider = new Slider();

    public void setupButtonPanel(PlotsPanel plotsPanel) {

        final int DEFAULT_PLOT_HEIGHT = 150;
        final int MIN_PLOT_HEIGHT = 100;
        final int MAX_PLOT_HEIGHT = 500;

        if (this.plotsPanel == null) {
            this.plotsPanel = plotsPanel;

            this.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);
            this.setVerticalAlignment(ALIGN_MIDDLE);

            changeLayout.addSelectHandler(new ChangeLayoutHandler());

            heightSlider.setValue(DEFAULT_PLOT_HEIGHT);
            heightSlider.setMaxValue(MAX_PLOT_HEIGHT);
            heightSlider.setMinValue(MIN_PLOT_HEIGHT);
            heightSlider.addValueChangeHandler(new HeightSliderValueChangeHandler());

            this.add(changeLayout);
            this.add(heightSlider);
        }
    }

    public Integer getPlotHeight() {
        return heightSlider.getValue();
    }

    private class ChangeLayoutHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            plotsPanel.changeLayout();
        }
    }

    private class HeightSliderValueChangeHandler implements ValueChangeHandler<Integer> {
        @Override
        public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
            plotsPanel.changeChildrenHeight(integerValueChangeEvent.getValue());
        }
    }
}
