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

package com.griddynamics.jagger.xml;

import com.griddynamics.jagger.providers.creators.ObjectCreator;

/**
 * @author Nikolay Musienko
 *         Date: 22.04.13
 */

public class RequestPathCvsWrapper implements ObjectCreator<RequestPath> {

    String[] header;

    @Override
    public RequestPath createObject(String... strings) {
        RequestPath ret = new RequestPath();
        if(header!=null){
            for (int i = 0; i < header.length; i++){
                if ( header[i].equals("host") ) {
                    ret.setHost(strings[i]);
                } else if( header[i].equals("path") ){
                    ret.setPath(strings[i]);
                }
            }
        } else {
            ret.setHost(strings[0]);
            ret.setPath(strings[1]);
        }
        return ret;
    }

    @Override
    public void setHeader(String[] header) {
        this.header = header;
    }
}
