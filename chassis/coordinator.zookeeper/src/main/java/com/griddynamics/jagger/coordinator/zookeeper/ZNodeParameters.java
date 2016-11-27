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

package com.griddynamics.jagger.coordinator.zookeeper;

import com.griddynamics.jagger.util.SerializationUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.data.ACL;

import java.util.Arrays;
import java.util.List;

/**
 * Parameters used when znode is creating.
 */
public class ZNodeParameters {
    private String path;
    private byte[] data = null;
    private List<ACL> acl = ZooDefs.Ids.OPEN_ACL_UNSAFE;
    private CreateMode createMode = CreateMode.PERSISTENT;

    public ZNodeParameters withPath(String path) {
        this.path = path;
        return this;
    }

    public ZNodeParameters ephemeralSequential() {
        this.path = "";
        this.createMode = CreateMode.EPHEMERAL_SEQUENTIAL;
        return this;
    }

    public ZNodeParameters persistentSequential() {
        this.path = "";
        this.createMode = CreateMode.PERSISTENT_SEQUENTIAL;
        return this;
    }

    public String getPath() {
        return path;
    }

    public byte[] getData() {
        return data;
    }

    public List<ACL> getAcl() {
        return acl;
    }

    public CreateMode getCreateMode() {
        return createMode;
    }

    public <C> ZNodeParameters withDataObject(C object) {
        this.data = SerializationUtils.serialize(object);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ZNodeParameters that = (ZNodeParameters) o;

        if (acl != null ? !acl.equals(that.acl) : that.acl != null) return false;
        if (createMode != that.createMode) return false;
        if (!Arrays.equals(data, that.data)) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (data != null ? Arrays.hashCode(data) : 0);
        result = 31 * result + (acl != null ? acl.hashCode() : 0);
        result = 31 * result + (createMode != null ? createMode.hashCode() : 0);
        return result;
    }
}
