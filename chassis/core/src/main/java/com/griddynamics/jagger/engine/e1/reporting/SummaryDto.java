package com.griddynamics.jagger.engine.e1.reporting;


/** Class is used to pass metric values to jasper report templates
 *
 * @details
 * Class has key,value for metric storage, and decision to define color of metric in report
 *
 * @author
 * Latnikov Dmitry
 */
public class SummaryDto {
    private String key;
    private String value;
    // default empty string is important - used in jrxml for style setup
    private String decision = "";

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getDecision() {
        return decision;
    }

    public void setDecision(String decision) {
        this.decision = decision;
    }

}
