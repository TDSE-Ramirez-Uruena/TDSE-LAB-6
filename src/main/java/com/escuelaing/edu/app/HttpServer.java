package com.escuelaing.edu.app;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class HttpServer {

    private static final int DEFAULT_PORT = 8080;
    private static boolean running = false;

    public static void start(int port) {
        running = true;

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Servidor HTTP iniciado y listo en el puerto: " + port);

            while (running) {
                try (Socket clientSocket = serverSocket.accept();
                     InputStream inStream = clientSocket.getInputStream();
                     OutputStream outStream = new BufferedOutputStream(clientSocket.getOutputStream());
                     BufferedReader in = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8))) {

                    String requestLine = in.readLine();
                    if (requestLine == null || requestLine.isEmpty()) {
                        continue;
                    }

                    // Consumir encabezados HTTP
                    String headerLine;
                    while ((headerLine = in.readLine()) != null && !headerLine.isEmpty()) {
                        // Consumo de headers
                    }

                    handleRequest(requestLine, outStream);

                } catch (IOException e) {
                    if (running) {
                        System.err.println("Error procesando conexión cliente: " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto " + port + ": " + e.getMessage());
        }

        System.out.println("Servidor detenido de manera secuencial (Graceful Shutdown).");
    }

    public static void stop() {
        running = false;
    }

    private static void handleRequest(String requestLine, OutputStream out) throws IOException {
        Request req = new Request(requestLine);
        Response resp = new Response();

        if (!req.getMethod().equals("GET")) {
            sendErrorResponse(out, "405 Method Not Allowed", "Método no soportado en esta versión.");
            return;
        }

        String path = req.getPath();

        // 1. Verificar si existe una ruta lambda registrada en Router
        if (Router.hasRoute(path)) {
            try {
                Route route = Router.getRoute(path);
                Object result = route.handle(req, resp);
                String body = result != null ? result.toString() : "";

                // Si el body parece un objeto/JSON o texto plano
                String contentType = resp.getContentType();
                if (body.startsWith("{") || body.startsWith("[")) {
                    contentType = "application/json; charset=UTF-8";
                }

                sendStringResponse(out, "200 OK", contentType, body);
            } catch (Exception e) {
                sendErrorResponse(out, "500 Internal Server Error", "Error ejecutando lambda: " + e.getMessage());
            }
            return;
        }

        // 2. Fallback: Intentar servir recurso estático
        byte[] fileData = StaticFileService.getFileAsBytes(path);

        if (fileData != null) {
            String contentType = StaticFileService.getContentType(path);
            sendOkBytesResponse(out, contentType, fileData);
            return;
        }

        // 3. Ninguno coincide: 404 Not Found
        sendErrorResponse(out, "404 Not Found", "Recurso no encontrado: " + path);
    }

    // --- MÉTODOS DE RESPUESTA HTTP ---

    private static void sendStringResponse(OutputStream out, String status, String contentType, String body) throws IOException {
        byte[] bodyBytes = body.getBytes(StandardCharsets.UTF_8);
        String headers = "HTTP/1.1 " + status + "\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + "Connection: close\r\n\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }

    private static void sendOkBytesResponse(OutputStream out, String contentType, byte[] body) throws IOException {
        String headers = "HTTP/1.1 200 OK\r\n"
                + "Content-Type: " + contentType + "\r\n"
                + "Content-Length: " + body.length + "\r\n"
                + "Connection: close\r\n\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(body);
        out.flush();
    }

    private static void sendErrorResponse(OutputStream out, String status, String message) throws IOException {
        String htmlBody = "<!doctype html><html><head><meta charset=\"UTF-8\"><title>" + status + "</title></head>"
                + "<body><h1>" + status + "</h1><p>" + message + "</p></body></html>";
        byte[] bodyBytes = htmlBody.getBytes(StandardCharsets.UTF_8);

        String headers = "HTTP/1.1 " + status + "\r\n"
                + "Content-Type: text/html; charset=UTF-8\r\n"
                + "Content-Length: " + bodyBytes.length + "\r\n"
                + "Connection: close\r\n\r\n";

        out.write(headers.getBytes(StandardCharsets.UTF_8));
        out.write(bodyBytes);
        out.flush();
    }
}