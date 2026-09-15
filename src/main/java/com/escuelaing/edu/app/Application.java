package com.escuelaing.edu.app;

import static com.escuelaing.edu.app.WebFramework.*;

public class Application {

    public static void main(String[] args) {

        // 1. Configurar ubicación de recursos estáticos
        staticfiles("public");

        // 2. Definir servicios dinámicos con Lambdas
        get("/hello", (req, resp) -> {
            String name = req.getValue("name");
            if (name == null || name.isBlank()) {
                name = "world";
            }
            String prefix = System.getenv().getOrDefault("GREETING_PREFIX", "Hello");
            return prefix + " " + name + "!";
        });

        get("/greeting", (req, resp) -> {
            String name = req.getValue("name");
            if (name == null || name.isBlank()) {
                name = "invitado";
            }
            return "{\"greeting\":\"Hello, " + name + "!\"}";
        });

        get("/square", (req, resp) -> {
            String valStr = req.getValue("value");
            if (valStr == null || valStr.isBlank()) {
                return "{\"error\":\"El parámetro value es obligatorio\"}";
            }
            try {
                double val = Double.parseDouble(valStr);
                return "{\"value\":" + val + ",\"square\":" + (val * val) + "}";
            } catch (NumberFormatException e) {
                return "{\"error\":\"El parámetro debe ser un número válido\"}";
            }
        });

        get("/pi", (req, resp) -> String.valueOf(Math.PI));

        // 3. Iniciar el servidor web
        start();
    }
}