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

package com.griddynamics.jagger.cloudshaper

import java.text.SimpleDateFormat

class SshExecutor {
    public dryRun = false;

    public SshExecutor() {}

    public SshExecutor(dryRun) {
        this.dryRun = dryRun;
    }

    def executeViaSsh = {
        context, command ->
        String commandLine = command(context)
        if(commandLine != null) {
            println("===> [" + timestamp(context) + "] Execute [" + commandLine + "] on [" + context.hostConfiguration + "]")
            return execute(context, String.format("ssh -i %s %s@%s \"%s\"",
                    context.hostConfiguration["keyName"], context.hostConfiguration["login"], context.hostConfiguration["address"], commandLine));
        } else {
            null
        }
    }

    def execute = {
        context, command ->
        if(dryRun) {
            return "DRY RUN - OK"
        } else {
            Process p = Runtime.getRuntime().exec((String)command)
            p.waitFor()
            return streamToString(p.getInputStream())
        }
    }

    def streamToString = {
        if (it != null) {
            Writer writer = new StringWriter()

            char[] buffer = new char[1024]
            try {
                Reader reader = new BufferedReader(new InputStreamReader(it, "UTF-8"))
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n)
                }
            } finally {
                it.close()
            }
            return writer.toString()
        }

        return "";
    }

    def static timestamp = {
        context ->
        def date = new Date();
        (new SimpleDateFormat("yyyy.MM.dd hh:mm:ss.SSS")).format(date) + String.format(" (+%.1fs)", (date.getTime() - context.absoluteStartTime)/1000.0 )
    }
}
