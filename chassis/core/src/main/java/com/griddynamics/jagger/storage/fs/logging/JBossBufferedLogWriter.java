package com.griddynamics.jagger.storage.fs.logging;

import org.jboss.serial.io.JBossObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

public class JBossBufferedLogWriter extends BufferedLogWriter {

    private final Logger log = LoggerFactory.getLogger(HessianBufferedLogWriter.class);

    @Override
    protected void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException {
        JBossObjectOutputStream out = new JBossObjectOutputStream(os);
        try {
            for (Serializable logEntry : fileQueue) {
                out.writeObject(logEntry);
            }
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
