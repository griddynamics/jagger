package com.griddynamics.jagger.storage.fs.logging;

import org.jboss.serial.io.JBossObjectOutputStream;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

public class JBossBufferedLogWriter extends BufferedLogWriter {

    @Override
    protected void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException {
        JBossObjectOutputStream out = new JBossObjectOutputStream(os);
        try {
            for (Serializable logEntry : fileQueue) {
                out.writeObject(logEntry);
            }
        } finally {
            out.close();
        }
    }
}
