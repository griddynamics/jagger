package com.griddynamics.jagger.webclient.server;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.HttpRequestHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

/**
 * Download service.
 */
public class FileDownloadRequestHandler implements HttpRequestHandler {

    private InMemoryFileStorage fileStorage;

    @Required
    public void setFileStorage(InMemoryFileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    @Override
    public void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        final int BUFFER = 1024 * 100;

        String fileKey = req.getParameter("fileKey");

        if (!fileStorage.exists(fileKey)) {
            String message = "File you asked not presented in Storage";
            resp.sendError(404, message);
            return;
        }

        byte[] fileInBytes = fileStorage.getFile(fileKey);

        resp.setContentType("application/octet-stream");
        resp.setHeader("Content-Disposition:", "attachment;filename=" + "\"" + fileKey + ".csv\"");
        OutputStream outputStream = resp.getOutputStream();

        resp.setBufferSize(BUFFER);
        resp.setContentLength(fileInBytes.length);

        outputStream.write(fileInBytes);

        outputStream.close();

        // delete file if necessary
        fileStorage.delete(fileKey);
    }
}
