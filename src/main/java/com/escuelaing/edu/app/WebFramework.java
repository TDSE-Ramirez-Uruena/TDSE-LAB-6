package com.escuelaing.edu.app;

public class WebFramework {

    public static void staticfiles(String folderPath) {
        StaticFileService.setWebRoot(folderPath);
    }

    public static void get(String path, Route route) {
        Router.addGetRoute(path, route);
    }

    public static void start() {
        String portVal = System.getenv("PORT");
        int port = (portVal != null && !portVal.isBlank()) ? Integer.parseInt(portVal) : 8080;

        // Habilitar la ruta de shutdown solo si estamos en entorno development
        String env = System.getenv().getOrDefault("APP_ENV", "development");
        if ("development".equalsIgnoreCase(env)) {
            get("/shutdown", (req, resp) -> {
                stop();
                return "Server will stop after this response.";
            });
        }

        HttpServer.start(port);
    }

    public static void stop() {
        HttpServer.stop();
    }
}