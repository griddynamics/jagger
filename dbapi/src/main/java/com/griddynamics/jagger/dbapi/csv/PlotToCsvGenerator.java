package com.griddynamics.jagger.dbapi.csv;

import com.griddynamics.jagger.dbapi.dto.PlotSingleDto;
import com.griddynamics.jagger.dbapi.dto.PointDto;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.List;

public class PlotToCsvGenerator {


    /**
     * Write csv file to OutputStream. OutputStream will be automatically closed.
     * @param lines        list of lines
     * @param xAxisLabel   x axis label
     * @param outputStream stream to write csv to
     * @throws IOException .
     */
    public static void generateCsvFile(List<PlotSingleDto> lines, String xAxisLabel, OutputStream outputStream) throws IOException {

        OutputStreamWriter osw = new OutputStreamWriter(outputStream);

        CsvListWriter writer = new CsvListWriter(osw, CsvPreference.STANDARD_PREFERENCE);

        try {
            String[] header = new String[lines.size() + 1]; // one for xAxis label
            header[0] = xAxisLabel;
            int i = 1;
            for (PlotSingleDto curve : lines) {
                header[i++] = curve.getLegend();
            }

            writer.writeHeader(header);

            i = 1; // first curve to first column
            for (PlotSingleDto curve : lines) {

                for (PointDto point : curve.getPlotData()) {
                    Double[] raw = new Double[lines.size() + 1]; // one for xAxis label
                    raw[0] = point.getX();
                    raw[i] = point.getY();

                    writer.write((Object[]) raw);
                }
                i++;
            }
        } finally {
            writer.flush();
            writer.close();
        }
    }
}
