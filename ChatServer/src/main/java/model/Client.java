package model;

import ConsolePart.SocketHandler;

public class Client {
    private String name;
    private Client recipient;
    private ChatInterface mysocket;
    private boolean isAgent = false;

    private Client(){}

    public Client(String name) {
        this.name = name;
    }

    public Client(String name, SocketHandler mysocket, boolean isAgent) {
        this.isAgent = isAgent;
        this.name = name;
        this.mysocket = mysocket;
    }

    public ChatInterface getSocket() {
        return mysocket;
    }

    public void setSocket(ChatInterface chatInterface){
        mysocket=chatInterface;
    }

    public boolean isAgent() {
        return isAgent;
    }

    public Client getRecipient() {
        return recipient;
    }

    public void setRecipient(Client recipient) {
        this.recipient = recipient;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new String(name) + " isAgent = " + isAgent;
    }
}
