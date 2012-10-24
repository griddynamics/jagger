package com.griddynamics.jagger.storage.fs.logging;

import com.caucho.hessian.io.Hessian2Output;

import java.io.IOException;
import java.io.ObjectOutput;
import java.io.OutputStream;

/**
 * @author mamontov
 */
public class HessianOutputStream extends OutputStream implements ObjectOutput {

    private final Hessian2Output hessian2Output;
    private final OutputStream out;

    public HessianOutputStream(OutputStream out) throws IOException {
        this.out = out;
        hessian2Output = new Hessian2Output(out);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        hessian2Output.writeObject(obj);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        hessian2Output.writeBytes(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        hessian2Output.writeBytes(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        hessian2Output.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeShort(int v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeChar(int v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeInt(int v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeLong(long v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeFloat(float v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeDouble(double v) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeBytes(String s) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeChars(String s) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void writeUTF(String s) throws IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void flush() throws IOException {
        hessian2Output.flush();
    }

    @Override
    public void close() throws IOException {
        hessian2Output.close();
    }
}
