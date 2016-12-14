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

package com.griddynamics.jagger.coordinator;

import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Random;

/**
 * Presents node identifier in cluster. Contains of type and some unique string identifier.
 *
 * @author Mairbek Khadikov
 */
public class NodeId implements Serializable {
    private final NodeType type;
    private final String identifier;

    public static NodeId masterNode() {
        return NodeId.of(NodeType.MASTER, "MASTER ["+getLocalHostAddress()+"]");
    }

    public static NodeId masterNode(String identifier) {
        return NodeId.of(NodeType.MASTER, identifier);
    }

    public static NodeId kernelNode(){
        return kernelNode(NodeType.KERNEL.toString() + "-" + new Random().nextInt() + " [" + getLocalHostAddress() + "]");
    }

    public static NodeId kernelNode(String identifier) {
        return NodeId.of(NodeType.KERNEL, identifier);
    }

    public static NodeId agentNode(String identifier) {
        return NodeId.of(NodeType.AGENT, identifier);
    }

    public static NodeId of(NodeType type, String identifier) {
        Preconditions.checkNotNull(type);
        Preconditions.checkNotNull(identifier);

        return new NodeId(type, identifier);
    }

    private static String getLocalHostAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }

    private NodeId(NodeType type, String identifier) {
        this.type = type;
        this.identifier = identifier;
    }

    public NodeType getType() {
        return type;
    }

    public String getIdentifier() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NodeId nodeId = (NodeId) o;

        if (!identifier.equals(nodeId.identifier)) return false;
        if (type != nodeId.type) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + identifier.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return type.name().toLowerCase() + "-" + identifier;
    }
}
