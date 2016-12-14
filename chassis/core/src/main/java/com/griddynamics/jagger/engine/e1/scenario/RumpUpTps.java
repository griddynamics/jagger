/*
 * Copyright (c) 2010-2012 Grid Dynamics Consulting Services, Inc, All Rights Reserved
 * http://www.griddynamics.com
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of
 * the Apache License; either
 * version 2.0 of the License, or any later version.
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

package com.griddynamics.jagger.engine.e1.scenario;

import org.bouncycastle.x509.NoSuchStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author Nikolay Musienko
 *         Date: 01.07.13
 */
public class RumpUpTps  implements DesiredTps {
    Logger log = LoggerFactory.getLogger(RumpUpTps.class);

    private final BigDecimal tps;
    private long warmUpTime;
    private long startTime = -1;
    private BigDecimal k;

    public RumpUpTps(BigDecimal tps, long warmUpTime) {
        this.tps = tps;
        this.warmUpTime = warmUpTime;
    }

    @Override
    public BigDecimal get(long time) {
        if(startTime == -1) {
            startTime = time;
            warmUpTime += time;
            k = tps.divide(new BigDecimal(warmUpTime - startTime), 10, RoundingMode.CEILING);
        }
        if (time > warmUpTime) {
            return tps;
        }
        BigDecimal currentTps = k.multiply(new BigDecimal(time - startTime));
        log.debug("Changing rate up to: {}", currentTps);
        return currentTps;
    }

    @Override
    public BigDecimal getDesiredTps() {
        return tps;
    }

    @Override
    public String toString() {
        return "RumpUpTps{" +
                "log=" + log +
                ", tps=" + tps +
                ", warmUpTime=" + warmUpTime +
                ", startTime=" + startTime +
                '}';
    }
}
