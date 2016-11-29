#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package suite.groovy.sources
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


import com.griddynamics.jagger.coordinator.NodeContext
import com.griddynamics.jagger.invoker.Scenario
import java.net.*
import com.griddynamics.jagger.invoker.ScenarioFactory

class GroovyExampleScenarioFactory<Q, R, E> implements ScenarioFactory<Q, R, E>, Serializable {
    URL url = new URL("http://rss.cnn.com/rss/edition.rss")

    Scenario get(NodeContext context) {
        return new Scenario() {
            @Override
            void doTransaction() {
                long before=System.currentTimeMillis()
                BufferedReader reader =null
                try{
                    getListener().onStart(url.path, null)
                    URLConnection con = url.openConnection()
                    reader = new BufferedReader(new InputStreamReader(con.getInputStream()))
                    StringBuilder sb=new StringBuilder();
                    String inputLine;
                    while ((inputLine = reader.readLine()) != null){
                        sb.append(inputLine)
                    }
                    reader.close()
                    getListener().onSuccess(null, url.path, sb.toString(),System.currentTimeMillis()-before)
                } catch (Throwable throwable) {
                    getListener().onError(null, null, throwable)
                } finally {
                  if (reader!=null){
                        reader.close()
                    }
                }
            }
        }
    }

    @Override
    int getCalibrationSamplesCount() {
        return 1
    }
}
