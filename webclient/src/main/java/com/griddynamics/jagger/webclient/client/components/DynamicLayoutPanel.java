package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import java.util.ArrayList;
import java.util.List;

/**
 * Panel that allows to represent child elements with one or two columns
 * @param <M> Type of child widgets. To make control from outside more comfortably */
public class DynamicLayoutPanel<M extends Widget> extends VerticalPanel {

    private Layout layout = Layout.ONE_COLUMN;

    public Layout getLayout() {
        return layout;
    }

    private final Widget stub;

    {
        Label label = new Label();
        label.setPixelSize(0, 0);
        label.setVisible(false);
        this.stub = label;
    }


    /**
     * Change layout by circle
     * @param layout - possible layout of DynamicLayoutPanel */
    @SuppressWarnings("unchecked")
    public void changeLayout(Layout layout) {

        if (this.layout == layout) // no need to change anything
            return;

        this.layout = layout;

        List<M> widgets = new ArrayList<M>();
        for (int i = 0; i < getWidgetCount(); i++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                Widget widget = hp.getWidget(j);
                if (widget != stub) {
                    // only widgets with type M can appear here
                    widgets.add((M)widget);
                }
            }
        }

        clear();

        for (M widget : widgets) {
            addChild(widget);
        }
    }

    /**
     * Add child widget
     * @param widget - child widget */
    public void addChild(M widget) {

        switch (layout) {
            case TWO_COLUMNS: addChildTwoColumns(widget);
                              break;

            default:          addChildOneColumn(widget);
        }
    }

    /**
     * Add child widget that will take all width of DynamicLayoutPanel
     * @param widget - child widget */
    private void addChildOneColumn(M widget) {
        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.add(widget);
        newHp.setCellWidth(widget, "100%");
        newHp.setSpacing(1);
        this.add(newHp);
    }

    /**
     * Add child widget that will half width of DynamicLayoutPanel
     * @param widget - child widget */
    private void addChildTwoColumns(Widget widget) {

        int totalCount = this.getWidgetCount();
        if (totalCount != 0) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(totalCount - 1); // get last horizontal Panel
            if (hp.getWidgetCount() == 2 && stub == hp.getWidget(1)) {
                hp.remove(stub);
                hp.add(widget);
                hp.setCellWidth(widget, "50%");
                return;
            }
        }

        HorizontalPanel newHp = new HorizontalPanel();
        newHp.setHorizontalAlignment(ALIGN_CENTER);
        newHp.setWidth("100%");
        newHp.add(widget);
        newHp.setCellWidth(widget, "50%");
        newHp.add(stub);
        newHp.setCellWidth(stub, "50%");
        newHp.setSpacing(1);
        this.add(newHp);
    }


    /**
     * Set height for all children
     * @param plotContainerHeight - height to set */
    public void changeChildrenHeight(String plotContainerHeight) {
        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j ++) {
                Widget widget = hp.getWidget(j);
                widget.setHeight(plotContainerHeight);
            }
        }
    }

    /**
     * @param id id of widget to remove (id of plot container) */
    @SuppressWarnings("unchecked")
    public void removeChild(String id) {
        if (layout == Layout.ONE_COLUMN) {
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                if (id.equals(hp.getWidget(0).getElement().getId())) {
                    remove(i);
                    return;
                }
            }
        } else {
            List<M> containers = new ArrayList<M>();
            for (int i = 0; i < getWidgetCount(); i ++) {
                HorizontalPanel hp = (HorizontalPanel) getWidget(i);
                for (int j = 0; j < hp.getWidgetCount(); j++) {
                    Widget widget = hp.getWidget(j);
                    if (!id.equals(widget.getElement().getId()) && widget != stub) {
                        // only widgets with type M can appear here, except stub.
                        containers.add((M)widget);
                    }
                }
            }
            clear();
            for (M pc : containers) {
                addChild(pc);
            }
        }
    }

    /**
     * Check if element with certain id contains in DynamicLayoutPanel
     * @param plotId - id of element to look for
     * @return - true if element was found, false - otherwise */
    public boolean containsElementWithId(String plotId) {

        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                if (plotId.equals(hp.getWidget(j).getElement().getId())) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Get all children of DynamicLayoutPanel
     * @return All widgets with <M> type*/
    @SuppressWarnings("unchecked")
    public List<M> getAllChildren() {

        List<M> result = new ArrayList<M>();
        for (int i = 0; i < getWidgetCount(); i ++) {
            HorizontalPanel hp = (HorizontalPanel) getWidget(i);
            for (int j = 0; j < hp.getWidgetCount(); j++) {
                Widget widget = hp.getWidget(j);
                if (widget != stub) {
                    // only widgets with type M can appear here
                    result.add((M)widget);
                }
            }
        }
        return result;
    }

    /**
     * Get first child of DynamicLayoutPanel
     * @return first child if contains, null if do not contains any */
    @SuppressWarnings("unchecked")
    public M getFirstChild() {

        if (getWidgetCount() == 0)
            return null;

        HorizontalPanel hp = (HorizontalPanel) getWidget(0);
        // only widgets with type M can appear here
        return (M) hp.getWidget(0);
    }

    /**
     * Enum of possible layouts of DynamicLayoutPanel*/
    public static enum Layout {
        ONE_COLUMN , TWO_COLUMNS;

        /**
         * @return next value of Layout enum by circle */
        Layout getNext() {
            return Layout.values()[(this.ordinal() + 1) % Layout.values().length];
        }
    }
}
