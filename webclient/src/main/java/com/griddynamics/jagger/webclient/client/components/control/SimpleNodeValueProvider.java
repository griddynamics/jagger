package com.griddynamics.jagger.webclient.client.components.control;

import com.griddynamics.jagger.dbapi.model.AbstractIdentifyNode;
import com.sencha.gxt.core.client.ValueProvider;

/**
 * Created with IntelliJ IDEA.
 * User: amikryukov
 * Date: 11/26/13
 */
public class SimpleNodeValueProvider implements ValueProvider<AbstractIdentifyNode, String> {

    @Override
    public String getValue(AbstractIdentifyNode object) {
        return object.getDisplayName();
    }

    @Override
    public void setValue(AbstractIdentifyNode object, String value) {
        object.setDisplayName(value);
    }

    @Override
    public String getPath() {
        return "displayName";
    }
}
