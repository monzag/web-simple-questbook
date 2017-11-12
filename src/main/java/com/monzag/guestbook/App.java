package com.monzag.guestbook;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class App
{
    public static void main( String[] args ) throws IOException {
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(9000), 0);

        httpServer.createContext("/guestbook", new Guestbook());
        httpServer.setExecutor(null);

        httpServer.start();
    }
}
