/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software Foundation; either
 * version 2.1 of the License, or any later version.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.griddynamics.jagger.agent.model;

/**
 * @author Nikolay Musienko
 *         Date: 05.07.13
 */

public class DisksData {

    long disksReadBytesTotal = 0;
    double disksQueueTotal = 0;
    double disksSvcTimeTotal = 0;
    long disksWriteBytesTotal = 0;

    public long getDisksReadBytesTotal() {
        return disksReadBytesTotal;
    }

    public void setDisksReadBytesTotal(long disksReadBytesTotal) {
        this.disksReadBytesTotal = disksReadBytesTotal;
    }

    public double getDisksQueueTotal() {
        return disksQueueTotal;
    }

    public void setDisksQueueTotal(double disksQueueTotal) {
        this.disksQueueTotal = disksQueueTotal;
    }

    public double getDisksSvcTimeTotal() {
        return disksSvcTimeTotal;
    }

    public void setDisksSvcTimeTotal(double disksSvcTimeTotal) {
        this.disksSvcTimeTotal = disksSvcTimeTotal;
    }

    public long getDisksWriteBytesTotal() {
        return disksWriteBytesTotal;
    }

    public void setDisksWriteBytesTotal(long disksWriteBytesTotal) {
        this.disksWriteBytesTotal = disksWriteBytesTotal;
    }

    @Override
    public String toString() {
        return "DisksData{" +
                "disksReadBytesTotal=" + disksReadBytesTotal +
                ", disksQueueTotal=" + disksQueueTotal +
                ", disksSvcTimeTotal=" + disksSvcTimeTotal +
                ", disksWriteBytesTotal=" + disksWriteBytesTotal +
                '}';
    }
}
