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

package com.griddynamics.jagger.test.target;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpService implements ServletContextListener {
    public static final int PORT = 5671;
    public static final int SIZE = 1024;
    public static final long COUNT = 10000000;

    public TcpService() {
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
    }

    static {
        Thread thread = new Thread() {
            @Override
            public void run() {
                for (;;) {
                    try {
                        ServerSocket serverSocket = new ServerSocket(PORT);
                        System.out.println("TcpService is started");
                        try {
                            for (;;) {
                                Socket socket = serverSocket.accept();
                                System.out.println("TcpService has new connection");
                                try {
                                    long receivedSize = 0;
                                    InputStream is = socket.getInputStream();
                                    long startTime = System.currentTimeMillis();
                                    int rate = 0;
                                    for (long i = 0; i < COUNT; ++i) {
                                        int size = is.read(new byte[SIZE]);
                                        receivedSize += size;
                                        rate += size;
                                        long time = System.currentTimeMillis();
                                        if (time - startTime >= 1000) {
                                            System.out.println(String.format("TCP inbound total, %d KiB/sec", rate / 1024));
                                            startTime = time;
                                            rate = 0;
                                        }
                                    }
                                    long sentSize = 0;
                                    OutputStream os = socket.getOutputStream();
                                    startTime = System.currentTimeMillis();
                                    rate = 0;
                                    for (long i = 0; i < COUNT; ++i) {
                                        os.write(new byte[SIZE]);
                                        sentSize += SIZE;
                                        rate += SIZE;
                                        long time = System.currentTimeMillis();
                                        if (time - startTime >= 1000) {
                                            System.out.println(String.format("TCP outbound total, %d KiB/sec", rate / 1024));
                                            startTime = time;
                                            rate = 0;
                                        }
                                    }
                                } finally {
                                    socket.close();
                                    System.out.println("TcpService connection is closed");
                                }
                            }
                        } finally {
                            serverSocket.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };
        thread.setDaemon(true);
        thread.start();
    }
}
