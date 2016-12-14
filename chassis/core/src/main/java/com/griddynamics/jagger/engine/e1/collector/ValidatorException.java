package com.griddynamics.jagger.engine.e1.collector;

import com.griddynamics.jagger.invoker.InvocationException;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 9/12/13
 * Time: 3:43 PM
 * To change this template use File | Settings | File Templates.
 */
public class ValidatorException extends InvocationException {

    private ResponseValidator validator;
    private Object result;

    public ValidatorException(ResponseValidator validator, Object result) {
        super(" validator " + validator.getName() + " failed ");

        this.validator = validator;
        this.result = result;
    }

    public ResponseValidator getValidator() {
        return validator;
    }

    public Object getResult() {
        return result;
    }
}
