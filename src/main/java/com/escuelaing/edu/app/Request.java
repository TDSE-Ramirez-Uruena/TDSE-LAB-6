package com.escuelaing.edu.app;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class Request {

    private final String method;
    private final String path;
    private final Map<String, String> queryParams = new HashMap<>();

    public Request(String rawRequestLine) {
        String[] parts = rawRequestLine.split(" ");
        this.method = parts.length > 0 ? parts[0] : "GET";

        String rawPath = parts.length > 1 ? parts[1] : "/";
        String extractedPath = "/";

        try {
            URI uri = new URI(rawPath);
            extractedPath = uri.getPath();
            parseQueryParams(uri.getQuery());
        } catch (URISyntaxException e) {
            extractedPath = rawPath;
        }

        this.path = extractedPath;
    }

    private void parseQueryParams(String query) {
        if (query == null || query.isBlank()) return;

        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                queryParams.put(entry[0], entry[1]);
            } else if (entry.length == 1) {
                queryParams.put(entry[0], "");
            }
        }
    }

    public String getMethod() { return method; }
    public String getPath() { return path; }

    public String getValue(String paramName) {
        return queryParams.get(paramName);
    }
}