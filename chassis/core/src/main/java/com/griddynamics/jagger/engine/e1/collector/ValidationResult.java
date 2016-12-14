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

package com.griddynamics.jagger.engine.e1.collector;

import java.io.Serializable;

public class ValidationResult implements Serializable {
    private final String name;
    private final int invoked;
    private final int failed;
    private final String displayName;

    public static ValidationResult create(String name, int invoked, int failed) {
        return new ValidationResult(name, null, invoked, failed);
    }

    public static ValidationResult create(String name, String displayName, int invoked, int failed) {
        return new ValidationResult(name, displayName, invoked, failed);
    }

    private ValidationResult(String name, String displayName, int invoked, int failed) {
        this.name = name;
        this.displayName = displayName;
        this.invoked = invoked;
        this.failed = failed;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getInvoked() {
        return invoked;
    }

    public int getFailed() {
        return failed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ValidationResult that = (ValidationResult) o;

        if (failed != that.failed) return false;
        if (invoked != that.invoked) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + invoked;
        result = 31 * result + failed;
        return result;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "name='" + name + '\'' +
                "displayName='" + displayName + '\'' +
                ", invoked=" + invoked +
                ", failed=" + failed +
                '}';
    }
}
