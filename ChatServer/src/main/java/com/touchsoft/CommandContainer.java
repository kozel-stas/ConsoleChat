package com.touchsoft;

public class CommandContainer {
    private String name;
    private boolean isAgent;
    private String command;
    private String message;
    private int serverinfo;

    public CommandContainer (int serverinfo, String name){
        this.name=name;
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
        serverinfo=-1;
    }

    public CommandContainer (String command) {
        this.isAgent = false;
        this.command = command;
        this.message = null;
        this.serverinfo=-1;
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

    public int getServerinfo() {
        return serverinfo;
    }

    @Override
    public String toString() {
        if(command!=null){
            return command;
        } else {
            if(serverinfo==-1) return name+" isAgent "+ isAgent+" "+message;
            else return name+" "+serverinfo;
        }
    }
}
