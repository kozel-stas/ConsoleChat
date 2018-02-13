package com.touchsoft;

import java.io.Serializable;

public class CommandContainer implements Serializable {
    private String name;
    private boolean isAgent;
    private String command;
    private String message;

    public CommandContainer (String name, boolean isAgent, String command, String message){
        this.name=name;
        this.isAgent=isAgent;
        this.command = command;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public String getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }

    public boolean isAgent() {
        return isAgent;
    }
}
