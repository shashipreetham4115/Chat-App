package server;

import server.services.ServerServices;

import java.io.IOException;
import java.net.ServerSocket;

public class ServerDriver {
    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(8000);
        ServerServices server = new Server(serverSocket);
        server.start();
    }
}