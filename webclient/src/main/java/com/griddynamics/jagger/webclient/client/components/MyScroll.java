package com.griddynamics.jagger.webclient.client.components;

import com.google.gwt.event.dom.client.*;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.NativeHorizontalScrollbar;


public class MyScroll extends NativeHorizontalScrollbar implements HasMouseWheelHandlers {

    {
        addMouseWheelHandler(new MouseWheelHandler() {
            @Override
            public void onMouseWheel(MouseWheelEvent event) {
                event.preventDefault();
            }
        });
    }

    @Override
    public HandlerRegistration addMouseWheelHandler(MouseWheelHandler handler) {
        return addDomHandler(handler, MouseWheelEvent.getType());
    }
}
