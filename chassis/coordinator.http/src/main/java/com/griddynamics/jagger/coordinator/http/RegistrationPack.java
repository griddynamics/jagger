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

import com.griddynamics.jagger.coordinator.Command;
import com.griddynamics.jagger.coordinator.NodeId;
import com.griddynamics.jagger.coordinator.Qualifier;

import java.io.Serializable;
import java.util.Set;

/**
 * Transfer object sent from remote node to server to register.
 *
 * @author Mairbek Khadikov
 */
public class RegistrationPack implements Serializable {
    private NodeId node;
    private Set<Qualifier<Command<Serializable>>> qualifiers;

    public static RegistrationPack create(NodeId node, Set<Qualifier<Command<Serializable>>> qualifiers) {
        RegistrationPack pack = new RegistrationPack();
        pack.node = node;
        pack.qualifiers = qualifiers;
        return pack;
    }

    public NodeId getNode() {
        return node;
    }

    public Set<Qualifier<Command<Serializable>>> getQualifiers() {
        return qualifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RegistrationPack that = (RegistrationPack) o;

        if (!node.equals(that.node)) return false;
        if (!qualifiers.equals(that.qualifiers)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = node.hashCode();
        result = 31 * result + qualifiers.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "RegistrationPack{" +
                "node=" + node +
                ", qualifiers=" + qualifiers +
                '}';
    }
}
