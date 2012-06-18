package com.griddynamics.jagger.webclient.client.data;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.datepicker.client.DateBox;

/**
 * Due to issue http://code.google.com/p/google-web-toolkit/issues/detail?id=4084
 * DateBox doesn't fire value change event when box is set blank.
 * EmptyDateBoxValueChangePropagator fixed this behavior.
 *
 * @author "Artem Kirillov" (akirillov@griddynamics.com)
 * @since 6/9/12
 */
public class EmptyDateBoxValueChangePropagator implements ValueChangeHandler<String> {
    private DateBox dateBox;

    public EmptyDateBoxValueChangePropagator() {
    }

    public EmptyDateBoxValueChangePropagator(DateBox dateBox) {
        this.dateBox = dateBox;
    }

    public void setDateBox(DateBox dateBox) {
        this.dateBox = dateBox;
    }

    @Override
    public void onValueChange(ValueChangeEvent<String> event) {
        if (dateBox != null && ("".equals(event.getValue()) || event.getValue() == null)) {
            ValueChangeEvent.fire(dateBox, null);
        }
    }
}
