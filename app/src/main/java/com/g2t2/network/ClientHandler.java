package com.g2t2.network;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class ClientHandler implements Runnable { // allows instances to be executed by a separate thread
    
     // keep track of all the clients - to send msg to all of them (broadcast to all)
    public static ArrayList<ClientHandler> clientHandlers = new ArrayList<>();
    private Socket socket;
    private BufferedReader bufferedReader; // used to read data 
    private BufferedWriter bufferedWriter; // used to send data to the client 
    private String clientName;

    public ClientHandler(Socket socket) {
        try {
            this.socket = socket;

            // need to wrap as OutputStream is a byte stream while streamWriter is a character stream
            // wrapping buffered writer makes it more efficient
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())); 
            bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream())); 
            this.clientName = bufferedReader.readLine(); // when user input 
            clientHandlers.add(this); // adding to the ArrayList
            broadcastMessage("SERVER " + clientName + " has entered the chat");
        } catch (IOException e) {
            closeEverything(socket, bufferedReader, bufferedWriter);
        }
    }
    
    // This method contains everything that is supposed to ran on a separate thread, such as blocking operations
    @Override
    public void run() {
        String messageFromClient;

        while (socket.isConnected()) {
            try {
                messageFromClient = bufferedReader.readLine(); // blocking operation, waiting for input from client
                broadcastMessage(messageFromClient);
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
                break; // break out from the while loop
            }
        }
    }

    public void broadcastMessage(String messageToSend) {
        for (ClientHandler clientHandler : clientHandlers) {
            try {
                if (!clientHandler.clientName.equals(clientName)) {
                    clientHandler.bufferedWriter.write(messageToSend); // sending message to the client, doesn't send a newLine character
                    clientHandler.bufferedWriter.newLine();
                    clientHandler.bufferedWriter.flush(); // this because the buffer will not be sent unless it is full 
                }
            } catch (IOException e) {
                closeEverything(socket, bufferedReader, bufferedWriter);
            }
        }
    }

    public void removeClientHandler() {
        clientHandlers.remove(this);
        broadcastMessage("SERVER: " + clientName + " has left the chat");
    }

    public void closeEverything(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter) {
        removeClientHandler();
        try {
            if (bufferedReader != null) {
                bufferedReader.close(); // closing the outer wrapper will close all inner wrapers
            }
            if (bufferedWriter != null) {
                bufferedWriter.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
