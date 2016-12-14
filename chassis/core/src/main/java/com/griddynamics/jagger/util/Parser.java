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

package com.griddynamics.jagger.util;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * UserGroup: dkotlyarov
 */
public final class Parser {

    private final static Pattern pattern = Pattern.compile("\\.\\.");

    private Parser() {
    }

    public static long parseTimeMillis(String time) {
        if (time.endsWith("s")) {
            return Long.parseLong(time.split("s")[0]) * 1000L;
        } else if (time.endsWith("m")) {
            return Long.parseLong(time.split("m")[0]) * 1000L * 60L;
        } else if (time.endsWith("h")) {
            return Long.parseLong(time.split("h")[0]) * 1000L * 60L * 60L;
        } else if (time.endsWith("d")) {
            return Long.parseLong(time.split("d")[0]) * 1000L * 60L * 60L * 24L;
        } else if (time.endsWith("w")) {
            return Long.parseLong(time.split("w")[0]) * 1000L * 60L * 60L * 24L * 7L;
        } else {
            return Long.parseLong(time);
        }
    }

    public static long parseTime(String time, Random random) {
        String[] items = pattern.split(time);
        if (items.length == 1) {
            return parseTimeMillis(time);
        } else {
            long min = parseTimeMillis(items[0]);
            long max = parseTimeMillis(items[1]);
            long delta = max - min;
            if (delta < 0) {
                throw new IllegalArgumentException();
            }
            return Math.abs(random.nextLong() % delta) + min;
        }
    }

    public static int parseInt(String value, Random random) {
        String[] items = pattern.split(value);
        if (items.length == 1) {
            return Integer.parseInt(value);
        } else {
            int min = Integer.parseInt(items[0]);
            int max = Integer.parseInt(items[1]);
            int delta = max - min;
            if (delta < 0) {
                throw new IllegalArgumentException();
            }
            return random.nextInt(delta) + min;
        }
    }

    public static long parseLong(String value, Random random) {
        String[] items = pattern.split(value);
        if (items.length == 1) {
            return Long.parseLong(value);
        } else {
            long min = Long.parseLong(items[0]);
            long max = Long.parseLong(items[1]);
            long delta = max - min;
            if (delta < 0) {
                throw new IllegalArgumentException();
            }
            return Math.abs(random.nextLong() % delta) + min;
        }
    }
}
