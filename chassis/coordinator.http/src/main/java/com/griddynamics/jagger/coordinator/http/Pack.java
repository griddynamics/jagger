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

package com.griddynamics.jagger.coordinator.http;

import com.google.common.collect.ImmutableList;
import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.util.SerializationUtils;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

/**
 * Unit of exchange between nodes..
 *
 * @author Mairbek Khadikov
 */
public class Pack implements Serializable {

    private ImmutableList<PackEntry<Command<Serializable>>> commands;
    private ImmutableList<PackEntry<Serializable>> results;

    private Pack() {
    }

    public static <E> Builder builder() {
        return new Builder();
    }

    public ImmutableList<PackEntry<Command<Serializable>>> getCommands() {
        return commands;
    }

    public ImmutableList<PackEntry<Serializable>> getResults() {
        return results;
    }

    public String serialize() {
        return SerializationUtils.toString(this);
    }

    public boolean isEmpty(){
        return commands.isEmpty() && results.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pack pack = (Pack) o;

        if (!commands.equals(pack.commands)) return false;
        if (!results.equals(pack.results)) return false;
        return true;
    }

    @Override
    public int hashCode() {
        int result = commands.hashCode();
        result = 31 * result + results.hashCode();
        return result;
    }

    public static class Builder {
        private ImmutableList.Builder<PackEntry<Command<Serializable>>> commands = ImmutableList.builder();
        private ImmutableList.Builder<PackEntry<Serializable>> results = ImmutableList.builder();

        private Builder() {
        }

        public Builder addCommand(UUID uuid, Command<Serializable> command) {
            return addCommand(PackEntry.create(uuid, command));
        }

        public Builder addCommand(PackEntry<Command<Serializable>> entry) {
            commands.add(entry);
            return this;
        }

        public Builder addAllCommands(List<PackEntry<Command<Serializable>>> entries) {
            commands.addAll(entries);
            return this;
        }

        public Builder addResult(UUID uuid, Serializable result) {
            return addResult(PackEntry.create(uuid, result));
        }

        public Builder addResult(PackEntry<Serializable> entry) {
            results.add(entry);
            return this;
        }

        public Pack buildFromString(String str) {
            return SerializationUtils.fromString(str);
        }

        public Builder addAllResults(List<PackEntry<Serializable>> entries) {
            results.addAll(entries);
            return this;
        }

        public Pack build() {
            Pack pack = new Pack();
            pack.commands = commands.build();
            pack.results = results.build();
            return pack;
        }
    }

    @Override
    public String toString() {
        return "Pack {commands=" + commands + ", results=" + results + '}';
    }
}