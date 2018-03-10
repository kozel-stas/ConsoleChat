package model;

import model.AnswerCode;

public class CommandContainer {

    private String name;
    private boolean isAgent;
    private String command;
    private String message;
    private AnswerCode serverinfo;

    public CommandContainer(AnswerCode serverinfo, String name) {
        this.name = name;
        this.isAgent = false;
        this.command = null;
        this.message = null;
        this.serverinfo = serverinfo;
    }

    public CommandContainer(String name, boolean isAgent, String message) {
        this.name = name;
        this.isAgent = isAgent;
        this.command = null;
        this.message = message;
        this.serverinfo = AnswerCode.MESSAGE;
    }

    public CommandContainer(String command) {
        this.isAgent = false;
        this.command = command;
        this.message = null;
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

    public AnswerCode getServerinfo() {
        return serverinfo;
    }

    @Override
    public String toString() {
        if (command != null) {
            return command;
        } else {
            if (serverinfo == AnswerCode.MESSAGE) return name + " isAgent " + isAgent + " " + message;
            else return name + " " + serverinfo;
        }
    }
}
