package com.escuelaing.edu.app;

import java.util.HashMap;
import java.util.Map;

public class Router {

    private static final Map<String, Route> getRoutes = new HashMap<>();

    public static void addGetRoute(String path, Route route) {
        getRoutes.put(path, route);
    }

    public static Route getRoute(String path) {
        return getRoutes.get(path);
    }

    public static boolean hasRoute(String path) {
        return getRoutes.containsKey(path);
    }
}