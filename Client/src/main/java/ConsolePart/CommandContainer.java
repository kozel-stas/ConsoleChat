package ConsolePart;


public final class CommandContainer {

    private String login;
    private Role role;
    private String message;
    private AnswerCode command;
    private AnswerCode serverInfo;

    public CommandContainer(String login,Role role ,AnswerCode serverInfo) {
        this.login = login;
        this.role = null;
        this.command = null;
        this.message = null;
        this.serverInfo = serverInfo;
    }

    public CommandContainer(String login, Role role, String message) {
        this.login = login;
        this.role = role;
        this.command = AnswerCode.MESSAGE;
        this.message = message;
        this.serverInfo = null;
    }

    public CommandContainer(AnswerCode command, String login, Role role) {
        this.role =role;
        this.command = command;
        this.login = login;
        this.message = null;
    }

    public String getLogin() {
        return login;
    }

    public AnswerCode getCommand() {
        return command;
    }

    public String getMessage() {
        return message;
    }

    public Role getRole() {
        return role;
    }

    public AnswerCode getServerInfo() {
        return serverInfo;
    }

    @Override
    public String toString() {
        if (command != null) {
            return "Command: " + command.toString() + " ,login: " + login;
        } else {
            if (serverInfo == AnswerCode.MESSAGE)
                return "Name: " + login + " ,Role: " + role.toString() + " ,message: " + message;
            else return "Login: "+login + " ,ServerInfo: " + serverInfo.toString();
        }
    }
}
