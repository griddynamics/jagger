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

public class TcpData {
    double tcpBound = 0;
    double tcpListen = 0;
    double tcpEstablished = 0;
    double tcpIdle = 0;
    double tcpSynchronizedReceived = 0;
    long tcpInboundTotal = 0;
    long tcpOutboundTotal = 0;

    public double getTcpBound() {
        return tcpBound;
    }

    public void setTcpBound(double tcpBound) {
        this.tcpBound = tcpBound;
    }

    public double getTcpListen() {
        return tcpListen;
    }

    public void setTcpListen(double tcpListen) {
        this.tcpListen = tcpListen;
    }

    public double getTcpEstablished() {
        return tcpEstablished;
    }

    public void setTcpEstablished(double tcpEstablished) {
        this.tcpEstablished = tcpEstablished;
    }

    public double getTcpIdle() {
        return tcpIdle;
    }

    public void setTcpIdle(double tcpIdle) {
        this.tcpIdle = tcpIdle;
    }

    public double getTcpSynchronizedReceived() {
        return tcpSynchronizedReceived;
    }

    public void setTcpSynchronizedReceived(double tcpSynchronizedReceived) {
        this.tcpSynchronizedReceived = tcpSynchronizedReceived;
    }

    public long getTcpInboundTotal() {
        return tcpInboundTotal;
    }

    public void setTcpInboundTotal(long tcpInboundTotal) {
        this.tcpInboundTotal = tcpInboundTotal;
    }

    public long getTcpOutboundTotal() {
        return tcpOutboundTotal;
    }

    public void setTcpOutboundTotal(long tcpOutboundTotal) {
        this.tcpOutboundTotal = tcpOutboundTotal;
    }

    @Override
    public String toString() {
        return "TcpData{" +
                "tcpBound=" + tcpBound +
                ", tcpListen=" + tcpListen +
                ", tcpEstablished=" + tcpEstablished +
                ", tcpIdle=" + tcpIdle +
                ", tcpSynchronizedReceived=" + tcpSynchronizedReceived +
                ", tcpInboundTotal=" + tcpInboundTotal +
                ", tcpOutboundTotal=" + tcpOutboundTotal +
                '}';
    }
}
