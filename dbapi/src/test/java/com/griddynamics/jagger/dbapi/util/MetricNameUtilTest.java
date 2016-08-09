package com.griddynamics.jagger.dbapi.util;

import com.griddynamics.jagger.dbapi.dto.MetricNameDto;
import com.griddynamics.jagger.dbapi.dto.SummaryMetricValueDto;
import com.griddynamics.jagger.dbapi.dto.SummarySingleDto;
import org.testng.annotations.Test;

import java.util.*;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

public class MetricNameUtilTest {

    @Test
    public void combineSynonymsEmptyTest() throws Exception {

        List<SummarySingleDto> input = Collections.emptyList();
        assertTrue(MetricNameUtil.combineSynonyms(input).isEmpty());
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void combineSynonymsNullTest() throws Exception {

        MetricNameUtil.combineSynonyms(null);
    }

    @Test
    public void combineSynonymsSimpleTest() throws Exception {

        SummarySingleDto ssd1 = new SummarySingleDto();
        MetricNameDto mnd1 = new MetricNameDto(null, "m1");
        mnd1.setMetricNameSynonyms(Arrays.asList("m2", "m3"));
        ssd1.setMetricName(mnd1);
        Set<SummaryMetricValueDto> values1 = new HashSet<SummaryMetricValueDto>();
        values1.add(createSummaryMetricValueDto(24, "val1"));
        values1.add(createSummaryMetricValueDto(35, "val2"));
        ssd1.setValues(values1);

        SummarySingleDto ssd2 = new SummarySingleDto();
        MetricNameDto mnd2 = new MetricNameDto(null, "m2");
        mnd2.setMetricNameSynonyms(Arrays.asList("m1", "m3"));
        ssd2.setMetricName(mnd2);
        Set<SummaryMetricValueDto> values2 = new HashSet<SummaryMetricValueDto>();
        values2.add(createSummaryMetricValueDto(26, "val3"));
        values2.add(createSummaryMetricValueDto(37, "val4"));
        ssd2.setValues(values2);

        List<SummarySingleDto> input = Arrays.asList(ssd1, ssd2);
        List<SummarySingleDto> output = MetricNameUtil.combineSynonyms(input);

        assertEquals(output.size(), 1, "input should be combined into single output");

        assertEquals(output.get(0).getMetricName(), mnd1, "output should contains first metric name dto");

        Set<SummaryMetricValueDto> outputValues = output.get(0).getValues();
        assertEquals(outputValues.size(), 4, "should be 4 summary values");

        Set<SummaryMetricValueDto> allSummaryMetricValues = new HashSet<SummaryMetricValueDto>();
        allSummaryMetricValues.addAll(values1);
        allSummaryMetricValues.addAll(values2);
        for (SummaryMetricValueDto sv: allSummaryMetricValues) {
            assertTrue(outputValues.contains(sv), "output values should contain all values from input");
        }
    }

    @Test
    public void combineSynonymsNoNeedTest() throws Exception {

        SummarySingleDto ssd1 = new SummarySingleDto();
        MetricNameDto mnd1 = new MetricNameDto(null, "m1");
        mnd1.setMetricNameSynonyms(Arrays.asList("m4", "m7"));
        ssd1.setMetricName(mnd1);
        Set<SummaryMetricValueDto> values1 = new HashSet<SummaryMetricValueDto>();
        values1.add(createSummaryMetricValueDto(24, "val1"));
        values1.add(createSummaryMetricValueDto(35, "val2"));
        ssd1.setValues(values1);

        SummarySingleDto ssd2 = new SummarySingleDto();
        MetricNameDto mnd2 = new MetricNameDto(null, "m2");
        ssd2.setMetricName(mnd2);
        Set<SummaryMetricValueDto> values2 = new HashSet<SummaryMetricValueDto>();
        values2.add(createSummaryMetricValueDto(26, "val3"));
        values2.add(createSummaryMetricValueDto(37, "val4"));
        ssd2.setValues(values2);

        List<SummarySingleDto> input = Arrays.asList(ssd1, ssd2);
        List<SummarySingleDto> output = MetricNameUtil.combineSynonyms(input);

        assertEquals(output.size(), 2, "input should not be combined");

        assertTrue(output.contains(ssd1), "input should not be combined");
        assertTrue(output.contains(ssd2), "input should not be combined");
    }


    private static SummaryMetricValueDto createSummaryMetricValueDto(long sessionId, String value) {
        SummaryMetricValueDto smvd1 = new SummaryMetricValueDto();
        smvd1.setSessionId(sessionId);
        smvd1.setValue(value);
        return smvd1;
    }
}
