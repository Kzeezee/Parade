package com.g2t2.network;

import java.io.Serializable;


/*
 * This class allows messages (Strings) to be send across the network
 */
public class Message implements Serializable{
    private String[] messageToSend;

    public Message(String[] messagetToSend) {
        this.messageToSend = messagetToSend;
    }

    public String[] getMessage() {
        return messageToSend;
    }
}
