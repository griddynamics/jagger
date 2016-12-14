package com.griddynamics.jagger.webclient.server;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple in memory file storage that store files as byte arrays.
 *
 * Truncated implementation of file storage because of jagger team decision.
 */
public class InMemoryFileStorage {

    private Map<String, byte[]> store;

    {
        store = new HashMap<String, byte[]>();
    }

    /**
     * Checks whether any file already stored in store by file-key
     *
     * @param path file-key to check
     * @return true if file-key already used, false otherwise
     */
    public boolean exists(String path) {
        return store.containsKey(path);
    }

    /**
     * Stores file in to the store by file-key
     *
     * @param path file-key to store file
     * @param file file as byte array
     */
    public void store(String path, byte[] file) {
        store.put(path, file);
    }

    /**
     * Returns file in bytes by path
     *
     * @param path file-key to get file
     * @return file as byte array
     */
    public byte[] getFile(String path) {
        return store.get(path);
    }

    /**
     * Delete a file.
     *
     * @param path      the path to delete.
     */
    void delete(String path) {
        store.remove(path);
    }
}
