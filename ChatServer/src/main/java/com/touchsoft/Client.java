package com.touchsoft;

public class Client {
    private String name;
    private Client recipient;
    private SocketHandler mysocket;
    private boolean isAgent=false;
    protected Client(){}

    public Client(String name,SocketHandler mysocket,boolean isAgent){
        this.isAgent=isAgent;
        this.name=name;
        this.mysocket=mysocket;
    }

    public SocketHandler getMysocket() {
        return mysocket;
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
        return new String(name) +" isAgent = "+isAgent;
    }
}
