package com.touchsoft;

public class Client {
    private String name;
    private Client recipient;
    private boolean isAgent = false;

    protected Client() {
    }

    public Client(String name, boolean isAgent) {
        this.isAgent = isAgent;
        this.name = name;
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
