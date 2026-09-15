package com.escuelaing.edu.app;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class StaticFileService {

    private static String webRoot = "public";

    public static void setWebRoot(String path) {
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        webRoot = path;
    }

    public static byte[] getFileAsBytes(String path) {
        if (path == null || path.equals("/")) {
            path = "/index.html";
        }

        if (path.contains("..")) {
            return null; // Prevención Path Traversal
        }

        String resourcePath = webRoot + (path.startsWith("/") ? path : "/" + path);

        InputStream stream = StaticFileService.class.getClassLoader().getResourceAsStream(resourcePath);
        if (stream == null) {
            stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }
        if (stream == null) {
            return null;
        }

        try (InputStream is = stream; ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            byte[] data = new byte[4096];
            int nRead;
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            return buffer.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    public static String getContentType(String path) {
        if (path == null || path.equals("/")) path = "/index.html";
        if (path.endsWith(".html") || path.endsWith(".htm")) return "text/html; charset=UTF-8";
        if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
        if (path.endsWith(".css")) return "text/css; charset=UTF-8";
        if (path.endsWith(".png")) return "image/png";
        if (path.endsWith(".jpg") || path.endsWith(".jpeg")) return "image/jpeg";
        if (path.endsWith(".ico")) return "image/x-icon";
        return "application/octet-stream";
    }
}