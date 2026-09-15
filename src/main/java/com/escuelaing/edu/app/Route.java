package com.escuelaing.edu.app;

@FunctionalInterface
public interface Route {
    Object handle(Request req, Response resp) throws Exception;
}