package com.griddynamics.jagger.invoker;

import java.io.Serializable;

/**
 * Encapsulates kernel info
 * @n
 * Created by Andrey Badaev
 * Date: 07/02/17
 */
public class KernelInfo implements Serializable {
    
    private int kernelId;
    private int kernelsNumber;
    
    private KernelInfo() { // for serialization only
    }
    
    public KernelInfo(int kernelId, int kernelsNumber) {
        if (kernelId < 0) {
            throw new IllegalStateException("kernel id can't be negative");
        }
        if (kernelsNumber <= 0) {
            throw new IllegalStateException("kernels number can't be <= 0");
        }
        if (kernelId >= kernelsNumber) {
            throw new IllegalStateException("kernel id can't be >= that total kernels number");
        }
        
        this.kernelId = kernelId;
        this.kernelsNumber = kernelsNumber;
    }
    
    public int getKernelId() {
        return kernelId;
    }
    
    public int getKernelsNumber() {
        return kernelsNumber;
    }
    
    @Override
    public String toString() {
        return "KernelInfo{" + "kernelId=" + kernelId + ", kernelsNumber=" + kernelsNumber + '}';
    }
}
