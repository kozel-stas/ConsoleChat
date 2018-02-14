package com.touchsoft;

import java.io.Serializable;

public class CommandContainer implements Serializable {
    private String name;
    private boolean isAgent;
    private String command;
    private String message;
    private String serverinfo;

    public CommandContainer (String serverinfo){
        this.name=null;
        this.isAgent=false;
        this.command=null;
        this.message=null;
        this.serverinfo=serverinfo;
    }

    public CommandContainer (String name, boolean isAgent, String message){
        this.name=name;
        this.isAgent=isAgent;
        this.command = null;
        this.message = message;
    }

    public CommandContainer (String command, String name) {
        this.name = name;
        this.isAgent = false;
        this.command = command;
        this.message = null;
        this.serverinfo = null;
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

    public String getServerinfo() {
        return serverinfo;
    }
}
