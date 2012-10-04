package com.griddynamics.jagger.storage.fs.logging;

import com.google.common.io.Files;
import com.griddynamics.jagger.storage.fs.LocalFileStorage;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author mamontov
 */
public class HessianBufferedLogWriterTest {

    @Test
    public void testConcurrent() throws Exception {
        final ConcurrentLogWriter logWriter = new ConcurrentLogWriter();
        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        final LocalFileStorage fileStorage = new LocalFileStorage();
        fileStorage.setWorkspace(Files.createTempDir().getName());
        logWriter.setFileStorage(fileStorage);
        for (int k = 0; k < 100; k++) {
            List<Callable<Void>> callables = new ArrayList<Callable<Void>>(100);

            for (int i = 0; i < k; i++) {
                callables.add(new Callable<Void>() {
                    @Override
                    public Void call() {
                        for (int j = 0; j < 10000; j++) {
                            logWriter.log("path", "String09" + j);
                        }
                        return null;
                    }
                });
            }
            logWriter.start();
            final long startTime = System.currentTimeMillis();
            final List<Future<Void>> futures = executorService.invokeAll(callables);
            for (Future<Void> future : futures) {
                future.get();
            }
            System.out.println(k + ";" + (System.currentTimeMillis() - startTime));
            logWriter.stop();
        }

    }

    @Test
    public void testBuffered() throws Exception {
        final HessianBufferedLogWriter logWriter = new HessianBufferedLogWriter();
        logWriter.setFlushSize(1000);
        final ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int k = 0; k < 100; k++) {
            List<Callable<Void>> callables = new ArrayList<Callable<Void>>(100);
            final LocalFileStorage fileStorage = new LocalFileStorage();
            fileStorage.setWorkspace(Files.createTempDir().getName());
            logWriter.setFileStorage(fileStorage);

            for (int i = 0; i < k; i++) {
                callables.add(new Callable<Void>() {
                    @Override
                    public Void call() {
                        for (int j = 0; j < 10000; j++) {
                            logWriter.log("path", "String09" + j);
                        }
                        return null;
                    }
                });
            }
            final long startTime = System.currentTimeMillis();
            final List<Future<Void>> futures = executorService.invokeAll(callables);
            for (Future<Void> future : futures) {
                future.get();
            }
            System.out.println(k + ";" + (System.currentTimeMillis() - startTime));
        }
    }
}
