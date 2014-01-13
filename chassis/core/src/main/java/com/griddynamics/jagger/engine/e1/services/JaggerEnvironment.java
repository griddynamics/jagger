package com.griddynamics.jagger.engine.e1.services;

/**
 * Created with IntelliJ IDEA.
 * User: kgribov
 * Date: 12/10/13
 * Time: 4:17 PM
 * To change this template use File | Settings | File Templates.
 */
public enum JaggerEnvironment {
    TEST("TestListener"),
    TEST_GROUP("TestGroupListener"),
    TEST_SUITE("TestSuiteListener");

    private String name;

    JaggerEnvironment(String name){
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
