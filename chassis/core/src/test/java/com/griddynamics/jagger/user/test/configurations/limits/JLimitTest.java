package com.griddynamics.jagger.user.test.configurations.limits;

import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowErrThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.LowWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.RefValue;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpWarnThresh;
import com.griddynamics.jagger.user.test.configurations.limits.auxiliary.UpErrThresh;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author asokol
 *         created 11/30/16
 */
public class JLimitTest {


    private Double defLwt;
    private Double defUwt;
    private Double defLet;
    private Double defUet;

    private Double lwt;
    private Double uwt;
    private Double let;
    private Double uet;

    private String metricName;
    private Double refValue;

    private JLimit limitsForBaseLine;
    private JLimit limitsForRefValue;
    private JLimit customLimitsForBaseLine;
    private JLimit customLimitsForRefValue;
    private JLimit limitsForBLMultDef;
    private JLimit limitsForRVMultDef;

    @Before
    public void setUp() throws Exception {

        defLwt = 1.0;
        defUwt = 1.0;
        defLet = 1.0;
        defUet = 1.0;

        lwt = 0.1D;
        let = 0.2D;
        uwt = 0.8D;
        uet = 0.9D;

        metricName = "No panic.";
        refValue = 42D;

        limitsForBaseLine = JLimitVsBaseline.builder(metricName)
                .build();
        limitsForRefValue = JLimitVsRefValue.builder(metricName, RefValue.of(refValue))
                .build();

        customLimitsForBaseLine = JLimitVsBaseline.builder(metricName)
                .withExactLimits(LowWarnThresh.of(lwt), LowErrThresh.of(let), UpWarnThresh.of(uwt), UpErrThresh.of(uet))
                .build();
        customLimitsForRefValue = JLimitVsRefValue.builder(metricName, RefValue.of(refValue))
                .withExactLimits(LowWarnThresh.of(lwt), LowErrThresh.of(let), UpWarnThresh.of(uwt), UpErrThresh.of(uet))
                .build();
    }

    @Test
    public void createLimits() throws Exception {
        assertNotNull(limitsForBaseLine);
        assertNotNull(limitsForRefValue);
    }

    @Test
    public void correctMetricName() throws Exception {
        assertEquals(limitsForBaseLine.getMetricName(), metricName);
    }

    @Test
    public void correctRefValue() throws Exception {
        assertEquals(((JLimitVsRefValue) limitsForRefValue).getRefValue(), refValue);
    }

    @Test
    public void correctLimits() throws Exception {
        assertEquals(customLimitsForBaseLine.getLowerErrorThreshold(), let);
        assertEquals(customLimitsForBaseLine.getLowWarnThresh(), lwt);
        assertEquals(customLimitsForBaseLine.getUpperErrorThreshold(), uet);
        assertEquals(customLimitsForBaseLine.getUpperWarningThreshold(), uwt);

        assertEquals(customLimitsForRefValue.getLowerErrorThreshold(), let);
        assertEquals(customLimitsForRefValue.getLowWarnThresh(), lwt);
        assertEquals(customLimitsForRefValue.getUpperErrorThreshold(), uet);
        assertEquals(customLimitsForRefValue.getUpperWarningThreshold(), uwt);

        assertEquals(limitsForBaseLine.getLowerErrorThreshold(), defLet);
        assertEquals(limitsForBaseLine.getLowWarnThresh(), defLwt);
        assertEquals(limitsForBaseLine.getUpperErrorThreshold(), defUet);
        assertEquals(limitsForBaseLine.getUpperWarningThreshold(), defUwt);

        assertEquals(limitsForRefValue.getLowerErrorThreshold(), defLet);
        assertEquals(limitsForRefValue.getLowWarnThresh(), defLwt);
        assertEquals(limitsForRefValue.getUpperErrorThreshold(), defUet);
        assertEquals(limitsForRefValue.getUpperWarningThreshold(), defUwt);
    }

    @Test(expected = IllegalArgumentException.class)
    public void multipleCreationForBaseline() throws Exception {
        limitsForBLMultDef = JLimitVsBaseline.builder(metricName)
                .withOnlyErrors(LowErrThresh.of(1.0), UpErrThresh.of(42D))
                .withOnlyWarnings(LowWarnThresh.of(1.0), UpWarnThresh.of(42D))
                .build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void multipleCreationForRefVal() throws Exception {
        limitsForRVMultDef = JLimitVsRefValue.builder(metricName, RefValue.of(refValue))
                .withOnlyLowerThresholds(LowWarnThresh.of(0.00001), LowErrThresh.of(0.1))
                .withOnlyUpperThresholds(UpWarnThresh.of(42D), UpErrThresh.of(42.5))
                .build();
    }


}