#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
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

package ${package}.groovy;

import com.google.common.io.Files;
import com.griddynamics.jagger.exception.TechnicalException;
import com.griddynamics.jagger.invoker.InvocationException;
import com.griddynamics.jagger.invoker.Invoker;
import groovy.lang.GroovyClassLoader;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.Charset;

public class GroovyInvokerDelegate implements Invoker, Serializable {
    private String script;
    private transient Invoker invoker;


    private Invoker instantiateDelegate() {
        try {
            GroovyClassLoader gcl = new GroovyClassLoader();
            Class clazz = gcl.parseClass(script);
            Object aScript = clazz.newInstance();
            return (Invoker) aScript;
        } catch (Exception e) {
            throw new TechnicalException(e);
        }
    }

    public void setScript(Resource script) {
        try {
            this.script = Files.toString(script.getFile(), Charset.defaultCharset());
        } catch (IOException e) {
            throw new TechnicalException(e);
        }
    }

    @Override
    public Object invoke(Object query, Object endpoint) throws InvocationException {
        if(invoker==null){
            synchronized (script){
                if(invoker==null){
                    invoker=instantiateDelegate();
                }
            }
        }
        return invoker.invoke(query,endpoint);
    }
}