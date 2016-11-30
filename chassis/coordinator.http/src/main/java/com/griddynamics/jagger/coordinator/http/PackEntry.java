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
package com.griddynamics.jagger.coordinator.http;

import java.io.Serializable;
import java.util.UUID;

public class PackEntry<T extends Serializable> implements Serializable {
    private UUID id;
    private T value;
    private Throwable exception;

    public static <T extends Serializable> PackEntry<T> create(UUID id, T value) {
        PackEntry<T> entry = new PackEntry<T>();
        entry.id = id;
        entry.value = value;
        entry.exception = null;
        return entry;
    }

    public static <T extends Serializable> PackEntry<T> fail(UUID id, Throwable exception) {
        PackEntry<T> entry = new PackEntry<T>();
        entry.id = id;
        entry.exception = exception;
        return entry;
    }

    public PackEntry() {
    }

    public UUID getId() {
        return id;
    }

    public T getValue() {
        return value;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PackEntry packEntry = (PackEntry) o;

        if (id == null || !id.equals(packEntry.id)) return false;
        if (value == null || !value.equals(packEntry.value)) {
            return false; // todo: unnecessary check. if id == id that value must be equal
        }
        return !(exception == null || !exception.equals(packEntry.exception));
    }

    @Override
    public int hashCode() {
        int result = id.hashCode();
        result = 31 * result + value.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "PackEntry{" +
                "id='" + id + '\'' +
                ", value=" + value +
                ", exception=" + exception +
                '}';
    }
}
