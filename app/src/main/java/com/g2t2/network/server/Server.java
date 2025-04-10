package com.g2t2.network.server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

import com.g2t2.util.Utility;

/**
 * The Server class is responsible for accepting incoming client connections.
 * It listens on a specified port and creates a new ClientHandler for each connected client.
 * Each ClientHandler runs on its own thread to enable concurrent gameplay for multiple clients.
 * The server acts as a middle man between the ClientHandler and the Client.
 * 
 * This class represents the entry point of the server application and keeps the server running
 * until it is manually shut down.
 * 
 * @author Ben
 */
public class Server {
    private ServerSocket serverSocket;
    private static int portNumber;

    /**
    * The main method that starts the server application.
    * Instantiates the server socket and begins listening for client connections.
    *
    * @param args command line arguments (not used).
    * @throws IOException if an I/O error occurs when opening the socket.
    */
    public static void main(String[] args) throws IOException {
        readServerConfig();
        ServerSocket serverSocket = new ServerSocket(portNumber);
        Server server = new Server(serverSocket);
        Utility.clearConsoleScreen();
        server.startServer();
    }

    /**
     * Server Constructor 
     * 
     * @param serverSocket the server socket that listens for incoming client connections.
     */
    public Server(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

     /**
     * Starts the server to accept incoming client connections.
     * For each connection, a new ClientHandler is created and run on its own thread.
     * This allows multiple clients to interact with the server concurrently.
     * Keeps running until the server is manually shut down.
     */
    private void startServer() {
        try {
            System.out.println("Server has started");
            while (!serverSocket.isClosed()) { // run until the server is closed
                /* waiting for a client to connect -> halting method, wait until someone connects
                when someone connects, a Socket object will be returned which can be used to communicate with the server */
                Socket socket = serverSocket.accept(); // Socket represents a connection from the server to the client
                
                ClientHandler clientHandler = new ClientHandler(socket);
                System.out.println("A new client has connected");

                Thread thread = new Thread(clientHandler);
                thread.start();
            } 
        } catch (IOException e) {
                closeServerSocket();
        }
    }

    /**
    * Closes the server socket and stops the server from accepting new connections.
    */
    private void closeServerSocket() { 
        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        System.out.println("Server has closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read server's address and port at project root. Set static server port as well in Server.java file.
     * @return Properties object containing key-value pairs from config file
     */
    public static Properties readServerConfig() {
        Properties prop = new Properties();
        System.out.println("Reading server config file from : " + Paths.get("").toAbsolutePath().getParent().resolve("Parade.config"));
        try (InputStream is = Files.newInputStream(Paths.get("").toAbsolutePath().getParent().resolve("Parade.config"))) {
            prop.load(is);
            Server.portNumber = Integer.parseInt(prop.getProperty("server-port"));
        } catch (FileNotFoundException e) {
            System.out.println("No configuration file found for server.");
        } catch (IOException e) {
            System.out.println("There was a problem reading the server configuration file.");
        } catch (NumberFormatException e) {
            System.out.println("Port number is invalid.");
        }
        return prop;
    }
}
