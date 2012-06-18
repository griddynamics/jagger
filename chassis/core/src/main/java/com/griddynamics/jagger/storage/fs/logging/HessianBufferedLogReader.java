package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Input;

import java.io.InputStream;

public class HessianBufferedLogReader extends BufferedLogReader {

    private static class HessianLogReaderInput extends Hessian2Input implements LogReaderInput {
        private HessianLogReaderInput(InputStream is) {
            super(is);
        }
    }

    protected LogReaderInput getInput(InputStream in) {
        return new HessianLogReaderInput(in);
    }
}
