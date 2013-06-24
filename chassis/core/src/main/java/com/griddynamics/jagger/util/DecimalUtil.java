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

import org.codehaus.groovy.runtime.typehandling.BigDecimalMath;

import java.math.BigDecimal;

public class DecimalUtil {
    private static final BigDecimal IMPRESSION = new BigDecimal(0.1f);

    private DecimalUtil() {

    }

    public static boolean areEqual(BigDecimal first, BigDecimal second) {
        return areEqual(first, second, IMPRESSION);
    }


    public static boolean areEqual(BigDecimal first, BigDecimal second, BigDecimal impression) {
        BigDecimal temp = BigDecimalMath.toBigDecimal(BigDecimalMath.abs(first.subtract(second)));
        return temp.compareTo(impression) <= 0;

    }

    public static int compare(BigDecimal first, BigDecimal second) {
        if (areEqual(first, second)) {
            return 0;
        }
        return first.compareTo(second);
    }

}
