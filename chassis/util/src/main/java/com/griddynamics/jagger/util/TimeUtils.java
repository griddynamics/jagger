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

package com.griddynamics.jagger.util;

import com.griddynamics.jagger.exception.TechnicalException;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

import java.math.BigDecimal;
import java.math.MathContext;

public class TimeUtils {
    private static final long MILLIS_IN_SECONDS = 1000L;
    private static final BigDecimal MILLISECONDS_FACTOR = new BigDecimal(MILLIS_IN_SECONDS);

    private static final PeriodFormatter formatter =  new PeriodFormatterBuilder()
                                                        .appendHours()
                                                        .appendSuffix(" h")
                                                        .appendSeparator(", ")
                                                        .appendMinutes()
                                                        .appendSuffix(" m")
                                                        .appendSeparator(", ")
                                                        .appendSeconds()
                                                        .appendSuffix(" s")
                                                        .appendSeparator(" and ")
                                                        .appendMillis()
                                                        .appendSuffix(" ms")
                                                        .toFormatter();

    public static String formatDuration(long duration) {
        return formatter.print(new Period(duration));
    }

    public static String formatDuration(BigDecimal duration) {
        if (duration != null) {
            return formatDuration(duration.multiply(MILLISECONDS_FACTOR).round(MathContext.DECIMAL64).longValue());
        } else {
            return "null";
        }
    }

    public static long secondsToMillis(long s) {
        return s * MILLIS_IN_SECONDS;
    }

    public static void sleepSeconds(int seconds) {
        sleepMillis(seconds * MILLIS_IN_SECONDS);
    }

    public static void sleepMillis(long seconds) {
        if (seconds == 0) {
            return;
        }
        try {
            Thread.sleep(seconds);
        } catch (InterruptedException e) {
            throw new TechnicalException(e);
        }
    }
}
