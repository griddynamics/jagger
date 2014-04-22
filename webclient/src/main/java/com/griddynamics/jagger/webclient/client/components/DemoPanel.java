package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;
import com.sencha.gxt.dnd.core.client.*;
import com.sencha.gxt.widget.core.client.Slider;
import com.sencha.gxt.widget.core.client.button.TextButton;
import com.sencha.gxt.widget.core.client.event.SelectEvent;

import java.util.Random;

public class DemoPanel extends Composite {
    interface DemoPanelUiBinder extends UiBinder<Widget, DemoPanel> {
    }

    private static DemoPanelUiBinder ourUiBinder = GWT.create(DemoPanelUiBinder.class);

    @UiField
    protected DynamicLayoutPanel pane;

    @UiField
    protected ScrollPanel scrollPanel;

    @UiField
    protected HorizontalPanel buttonPanel;

    private static int index = 1;

    private Integer plotContainerHeight = 200;

    public DemoPanel() {
        initWidget(ourUiBinder.createAndBindUi(this));

        setUpButtonPanel();
        setUpPane();
    }

    private void setUpPane() {

        pane.setBorderWidth(3);
    }

    private void setUpButtonPanel() {

        buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_LEFT);

        TextButton addButton = new TextButton("Add container");
        addButton.addSelectHandler(new AddSelectHandler());

        TextButton removeAllButton = new TextButton("Remove all");
        removeAllButton.addSelectHandler(new RemoveAllHandler());

        TextButton removeLast = new TextButton("Remove last");
        removeLast.addSelectHandler(new RemoveLastHandler());

        TextButton removeRandom = new TextButton("Remove random");
        removeRandom.addSelectHandler(new RemoveRandomHandler());

        TextButton changeLayout = new TextButton("Change layout");
        changeLayout.addSelectHandler(new ChangeLayoutHandler());

        TextButton oneColumnLButton = new TextButton("One column");
        oneColumnLButton.addSelectHandler(new OneColumnLHandler());

        TextButton twoColumnsLButton = new TextButton("Two columns");
        twoColumnsLButton.addSelectHandler(new TwoColumnsHandler());

        Slider heightSlider = new Slider();
        heightSlider.setValue(plotContainerHeight);
        heightSlider.setMaxValue(500);
        heightSlider.setMinValue(100);
        heightSlider.addValueChangeHandler(new HeightSliderValueChangeHandler());


        buttonPanel.add(addButton);
        buttonPanel.add(removeAllButton);
        buttonPanel.add(removeLast);
        buttonPanel.add(removeRandom);
        buttonPanel.add(changeLayout);
        buttonPanel.add(oneColumnLButton);
        buttonPanel.add(twoColumnsLButton);
        buttonPanel.add(heightSlider);

        buttonPanel.setBorderWidth(3);
    }

    private void addSimplePanel(int i) {

        String id = "container " + i;
        PlotContainer plotContainer = new PlotContainer(id, id, chooseDemoStyle(i));
        plotContainer.setHeight(plotContainerHeight + "px");

        DropTarget target = new DropTarget(plotContainer) {
            @Override
            protected void onDragDrop(DndDropEvent event) {
                super.onDragDrop(event);
                PlotContainer incoming = (PlotContainer) event.getData();
                PlotContainer current = (PlotContainer) component;

                swap(incoming, current);
            }
        };

        target.setFeedback(DND.Feedback.BOTH);


        new DragSource(plotContainer) {
            @Override
            protected void onDragStart(DndDragStartEvent event) {
                super.onDragStart(event);
                // by default drag is allowed
                event.setData(this.widget);
            }
        };

        pane.addChild(plotContainer);
    }

    private void swap(PlotContainer c1 , PlotContainer c2) {
        Label l1 = c1.getLabel();
        Label l2 = c2.getLabel();

        c1.setLabel(l2);
        c2.setLabel(l1);
    }

    private String chooseDemoStyle(int i) {

        switch (i % 2) {
            case 0 : return JaggerResources.INSTANCE.css().getDemoStyle1();
            default : return JaggerResources.INSTANCE.css().getDemoStyle2();
        }
    }

    public void startDemo() {
        //
    }

    private class AddSelectHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            addSimplePanel(index ++);
            scrollPanel.scrollToBottom();
        }
    }

    private class RemoveAllHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            pane.clear();
        }
    }

    private class RemoveLastHandler implements SelectEvent.SelectHandler {

        @Override
        public void onSelect(SelectEvent event) {
            pane.removeLast();
        }
    }

    private class ChangeLayoutHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            int size = DynamicLayoutPanel.Layout.values().length;
            DynamicLayoutPanel.Layout layout = DynamicLayoutPanel.Layout.values()[(pane.getLayout().ordinal() + 1) % size];
            pane.changeLayout(layout);
            scrollPanel.scrollToBottom();
        }
    }

    private class OneColumnLHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            pane.changeLayout(DynamicLayoutPanel.Layout.ONE_COLUMN);
        }
    }

    private class TwoColumnsHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {
            pane.changeLayout(DynamicLayoutPanel.Layout.TWO_COLUMNS);
        }
    }

    private class HeightSliderValueChangeHandler implements ValueChangeHandler<Integer> {
        @Override
        public void onValueChange(ValueChangeEvent<Integer> integerValueChangeEvent) {
            plotContainerHeight = integerValueChangeEvent.getValue();
            pane.changeChildrenHeight(plotContainerHeight + "px");
        }
    }

    // ** for demo
    private Random random = new Random(index);
    private class RemoveRandomHandler implements SelectEvent.SelectHandler {
        @Override
        public void onSelect(SelectEvent event) {

            int randomInt = random.nextInt(index - 1) + 1;
            pane.removeChild("container " + randomInt);
        }
    }
}
