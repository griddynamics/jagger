package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Output;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Collection;

public class HessianBufferedLogWriter extends BufferedLogWriter {

    protected void log(Collection<Serializable> fileQueue, OutputStream os) throws IOException {
        Hessian2Output objectStream = new Hessian2Output(os);
        try {
            for (Serializable logEntry : fileQueue) {
                objectStream.writeObject(logEntry);
            }
        } finally {
            objectStream.close();
        }
    }
}
