package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.resources.client.ImageResource;
import com.sencha.gxt.core.client.ValueProvider;
import com.sencha.gxt.data.shared.TreeStore;
import com.sencha.gxt.widget.core.client.event.BeforeCheckChangeEvent;
import com.sencha.gxt.widget.core.client.event.BeforeCollapseItemEvent;
import com.sencha.gxt.widget.core.client.event.BeforeExpandItemEvent;
import com.sencha.gxt.widget.core.client.event.CheckChangeEvent;
import com.sencha.gxt.widget.core.client.tree.Tree;

/**
 * Abstract tree that allows check all children, check parent with no additional events.
 * @param <M> the model type
 * @param <C> the cell data type
 */
public abstract class AbstractTree<M, C> extends Tree<M, C> {

    /**
     * boolean disabled tree or not
     * uses for canceling events
     */
    protected boolean disabled;


    /**
     * Constructor matches super class
     */
    public AbstractTree(TreeStore<M> store, ValueProvider<? super M, C> valueProvider) {
        super(store, valueProvider);
    }


    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    {
        // runs after any constructor

        this.setCheckable(true);
        this.setCheckStyle(Tree.CheckCascade.NONE);
        this.setCheckNodes(Tree.CheckNodes.BOTH);

        this.addBeforeExpandHandler(new BeforeExpandItemEvent.BeforeExpandItemHandler<M>() {
            @Override
            public void onBeforeExpand(BeforeExpandItemEvent<M> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCollapseHandler(new BeforeCollapseItemEvent.BeforeCollapseItemHandler<M>() {
            @Override
            public void onBeforeCollapse(BeforeCollapseItemEvent<M> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addBeforeCheckChangeHandler(new BeforeCheckChangeEvent.BeforeCheckChangeHandler<M>() {
            @Override
            public void onBeforeCheckChange(BeforeCheckChangeEvent<M> event) {
                if (disabled)
                    event.setCancelled(true);
            }
        });

        this.addCheckChangeHandler(new CheckChangeEvent.CheckChangeHandler<M>() {
            @Override
            public void onCheckChange(CheckChangeEvent<M> event) {

                AbstractTree.this.disableEvents();

                CheckState state = event.getChecked();
                M item = event.getItem();

                AbstractTree.this.setStateToSubTree(item, state);
                if (state.equals(CheckState.CHECKED)) {
                    AbstractTree.this.checkParent(item);
                    AbstractTree.this.setExpanded(item, true, false);
                } else {
                    unCheckParent(item);
                }

                AbstractTree.this.enableEvents();
                check(item, state);
            }
        });
    }

    /**
     * SetCheckState without firing any events
     * @param item model
     * @param state check state
     */
    public void setCheckedNoEvents(M item, CheckState state) {
        disableEvents();
        setChecked(item, state);
        enableEvents();
    }

    /**
     * Check subtree
     * @param item root ir=tem for subtree
     * @param state state to be set to subtree
     */
    protected void setStateToSubTree(M item, CheckState state) {
        if (store.hasChildren(item))
            for (M child : store.getChildren(item)) {
                setChecked(child, state);
                setStateToSubTree(child, state);
            }
    }

    /**
     * Check parent.
     * Check state of parent = PARTIAL if parent has unchecked or partial checked children, and CHECKED otherwise.
     * @param item item witch parent should be checked
     */
    protected void checkParent(M item) {
        M parent = store.getParent(item);
        if (parent == null) return;

        boolean hasUnchecked = false;

        for (M ch : store.getChildren(parent)) {
            if (!isChecked(ch) || CheckState.PARTIAL.equals(getChecked(ch))) {
                setChecked(parent, CheckState.PARTIAL);
                hasUnchecked = true;
                break;
            }
        }

        if (!hasUnchecked)
            setChecked(parent, CheckState.CHECKED);

        checkParent(parent);
    }

    /**
     * Uncheck parent.
     * Check state of parent = PARTIAL if parent has checked or partial checked children, and UNCHECKED otherwise.
     * @param item item witch parent should be checked
     */
    protected void unCheckParent(M item) {
        M parent = store.getParent(item);
        if (parent == null) return;
        boolean hasChecked = false;
        for (M ch : store.getChildren(parent)) {
            if (AbstractTree.this.getChecked(ch).equals(CheckState.CHECKED) || this.getChecked(ch).equals(CheckState.PARTIAL)) {
                this.setChecked(parent, CheckState.PARTIAL);
                hasChecked = true;
                break;
            }
        }
        if (!hasChecked)
            this.setChecked(parent, CheckState.UNCHECKED);

        unCheckParent(parent);
    }


    /**
     * No images in tree
     */
    @Override
    protected ImageResource calculateIconStyle(M model) {
        return null;
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void disable() {
        super.disable();
        setDisabled(true);
    }

    /**
     * disable ability to check/unCheck, collapse/expand actions
     */
    @Override
    public void enable() {
        super.enable();
        setDisabled(false);
    }

    public void enableTree() {
        this.enable();
        this.enableEvents();
    }

    public void clearStore() {
        store.clear();
    }

    /**
     * return false if CheckState = Tree.CheckState.UNCHECKED
     *        true in other cases
     * @param model tree model
     * @return bool
     */
    public boolean isChosen(M model) {
        return !CheckState.UNCHECKED.equals(getChecked(model));
    }

    /**
     * Set check state for item with specified id
     * @param elementId id of item to set check state
     * @param checkState state to set
     */
    public void setCheckState(String elementId, CheckState checkState) {

        M model = getStore().findModelWithKey(elementId);
        setChecked(model, checkState);
    }

    /**
     * Check event can goes here
     * @param item item that has been checked
     * @param state new check state of item in the tree
     */
    protected abstract void check(M item, CheckState state);
}
