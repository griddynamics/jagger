package com.griddynamics.jagger.storage.fs.logging;

import com.griddynamics.jagger.storage.FileStorage;
import org.jboss.serial.io.JBossObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

public class JBossBufferedLogWriter extends BufferedLogWriter {

    private final Logger log = LoggerFactory.getLogger(JBossBufferedLogWriter.class);

    public JBossBufferedLogWriter(int flushSize, int bufferSize, FileStorage fileStorage) {
        super(flushSize, bufferSize, fileStorage);
    }

    private static class JBossLogWriterOutput implements LogWriterOutput {
        private final JBossObjectOutputStream out;

        private JBossLogWriterOutput(OutputStream out) throws IOException {
            this.out = new JBossObjectOutputStream(out);
        }

        @Override
        public void writeObject(Object object) throws IOException {
                out.writeObject(object);
        }

        @Override
        public void close() throws IOException {
            out.close();
        }
    }

    @Override
    public LogWriterOutput getOutput(OutputStream out) throws IOException {
        return new JBossLogWriterOutput(out);
    }

}
