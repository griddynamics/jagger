package com.griddynamics.jagger.storage.fs.logging;

import com.google.common.base.Throwables;
import org.jboss.serial.io.JBossObjectInputStream;

import java.io.IOException;
import java.io.InputStream;

public class JBossBufferedLogReader extends BufferedLogReader {
    private static class JBossLogReaderInput implements LogReaderInput {

        private JBossObjectInputStream in;

        private JBossLogReaderInput(InputStream is) {
            try {
                in = new JBossObjectInputStream(is);
            } catch (IOException e) {
                throw Throwables.propagate(e);
            }
        }

        @Override
        public Object readObject() throws IOException {
            try {
                Object result = in.readObject();
                return result; 
            } catch (ClassNotFoundException e) {
                throw Throwables.propagate(e);
            }
        }
    }

    protected LogReaderInput getInput(InputStream in) {
        return new JBossLogReaderInput(in);
    }
}
