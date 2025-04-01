package com.g2t2.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private boolean isRunning = true;

    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    private void startServer() {
        try {
            System.out.println("Server has started");
            while (isRunning) { // run until the server is closed
                // waiting for a client to connect -> halting method, wait until someone connects
                // when someone connects, a Socket object will be returned which can be used to communicate with the client
                Socket socket = serverSocket.accept(); // Socket represents a connection from the server to the client
                
                // allows for multi-treading
                // when a class implements the Runnable interface, the instance of the class will be executed by a separate thread
                // this also means that you need to create a new Thread Object 
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println("A new client has connected");

                Thread thread = new Thread(clientHandler);
                thread.start();
            } 
        } catch (IOException e) {
                closeServerSocket();
        }
    }

    // shutting down the serverSocket
    private void closeServerSocket() { 
        isRunning = false;
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(1234);
        Server server = new Server(serverSocket);
        server.startServer();
    }
}
