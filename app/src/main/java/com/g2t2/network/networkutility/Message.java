package com.g2t2.network.networkutility;

import java.io.Serializable;

/**
 * This Message class represents a message object that can be transmitted over a network. 
 * It encapsulates a String array, typically used for sending commands or data between the client and server. 
 * 
 * This class implements {@link Serializable} to allow object transfer via sockets
 * 
 * @author Ben
 */
public class Message implements Serializable {
    private String[] messageToSend; // The message to be sent over the network

    /**
     * Message Constructor
     *
     * @param messagetToSend the string array representing the message content
     */
    public Message(String[] messagetToSend) {
        this.messageToSend = messagetToSend;
    }

    /**
     * Retrieves the message contained in this object.
     *
     * @return a String array representing the message sent
     */
    public String[] getMessage() {
        return messageToSend;
    }
}
