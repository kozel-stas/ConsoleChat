package com.touchsoft;

public class CommandContainer {
    private String name;
    private boolean isAgent;
    private String command;
    private String message;
    private String serverinfo;

    public CommandContainer (String serverinfo, String name){
        this.name="Server";
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

    public CommandContainer (String command) {
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

    @Override
    public String toString() {
        if(command==null){
            return command;
        } else {
            return name+" isAgent "+ isAgent+" "+message;
        }
    }
}
