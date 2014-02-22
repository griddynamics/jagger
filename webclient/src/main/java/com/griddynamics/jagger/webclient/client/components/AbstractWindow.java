package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.user.client.ui.DialogBox;
import com.griddynamics.jagger.webclient.client.resources.JaggerResources;

/**
 * Created with IntelliJ IDEA.
 * User: mnovozhilov
 * Date: 2/20/14
 * Time: 12:34 PM
 * To change this template use File | Settings | File Templates.
 */
public abstract class AbstractWindow extends DialogBox {
    protected int width = 600;
    protected int height = 500;
    public AbstractWindow(String width, String height){
        setSize(width,height);
    }

    public AbstractWindow(){
        addStyleName(JaggerResources.INSTANCE.css().abstractWindow());
        setPixelSize(width,height);
        setPopupPosition(300, 100);
    }

}
