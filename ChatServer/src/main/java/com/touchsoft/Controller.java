package com.touchsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

//класс, отвечающий за обработку сообщений.
public class Controller {
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private Client client = null;
    private SocketHandler socket;
    private boolean waitAgent = false;
    private List<CommandContainer> bufferedMessage = null;

    private static Pattern registerOrLoginAgentOrClientPattern = Pattern.compile("^\\/(register|login) (agent|client) [A-z0-9]*$");
    private static Pattern registerPattern = Pattern.compile("^\\/register ");
    private static Pattern loginPattern = Pattern.compile("^\\/login ");
    private static Pattern leavePattern = Pattern.compile("^\\/leave$");
    private static Pattern exitPattern = Pattern.compile("^\\/exit$");
    private static Pattern registerOrLoginClientPatter = Pattern.compile("^\\/(register|login) client ");
    private static Pattern registerOrLoginAgentPatter = Pattern.compile("^\\/(register|login) agent ");

    public Controller(SocketHandler socket) {
        this.socket = socket;
    }

    public void handler(CommandContainer container) {
        log.info("request " + container.toString());
        if (client == null) {
            if (container.getCommand() != null) {
                handlerCommand(container);
            } else {
                socket.send(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN, "Server"));
            }
        } else {
            if (container.getCommand() != null) {
                handlerCommand(container);
            } else {
                if (container.getMessage() != null) {
                    handlerMessage(container);
                } else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer(AnswerCode.UNKNOWN_MISTAKE, "server"));
                }
            }
        }
    }

    private void handlerCommand(CommandContainer container) {
        String command = container.getCommand();
        if (client == null) {
            if (registerPattern.matcher(command).find()) {
                register(command);
                return;
            } else {
                if (loginPattern.matcher(command).find()) {
                    login(command);
                    return;
                } else {
                    if (leavePattern.matcher(command).find())
                        socket.send(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN, "Server"));
                    else if (exitPattern.matcher(command).find())
                        socket.send(new CommandContainer(AnswerCode.EXIT, "Server"));
                    else {
                        log.warn("unknown command " + container.toString());
                        socket.send(new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
                        return;
                    }
                }
            }
        } else if (leavePattern.matcher(command).find()) {
            if (client.isAgent())
                socket.send(new CommandContainer(AnswerCode.CAN_NOT_LEAVE_AGENT, "Server"));
            else {
                if (client.getRecipient() != null) {
                    leave();
                    socket.send(new CommandContainer(AnswerCode.LEAVE_CHAT, "Server"));
                } else socket.send(new CommandContainer(AnswerCode.DONT_HAVE_CHAT, "Server"));
            }
        } else {
            if (exitPattern.matcher(command).find()) {
                if (client.getRecipient() != null) {
                    leave();
                    socket.send(new CommandContainer(AnswerCode.EXIT, "Server"));
                } else socket.send(new CommandContainer(AnswerCode.EXIT, "Server"));
            } else {
                if (registerPattern.matcher(command).find() || loginPattern.matcher(command).find())
                    socket.send(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET, "Server"));
                else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
                }
            }
        }
    }

    private void handlerMessage(CommandContainer container) {
        if (client.getRecipient() != null) {
            client.getRecipient().getMysocket().send(container);
        } else {
            if (client.isAgent()) socket.send(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT, "Server"));
            else {
                if (waitAgent) {
                    if (bufferedMessage == null) bufferedMessage = new ArrayList<>();
                    bufferedMessage.add(container);
                    socket.send(new CommandContainer(AnswerCode.FIRST_AGENT_ANSWER_YOU, "Server"));
                } else {
                    if (FindAgentSystem.findSystem(client)) {
                        log.info("start conversation " + client.toString() + " " + client.getRecipient().toString());
                        client.getRecipient().getMysocket().send(container);
                    } else {
                        bufferedMessage = new ArrayList<>();
                        bufferedMessage.add(container);
                        socket.send(new CommandContainer(AnswerCode.NO_AGENT_WAIT, "Server"));
                    }
                }
            }
        }
    }

    private void login(String command) {
        if (registerOrLoginAgentPatter.matcher(command).find()) loginAgent(command);
        else if (registerOrLoginClientPatter.matcher(command).find()) loginUser(command);
        else {
            socket.send(new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER, "Server"));
            log.warn("unknown type of user", command);
        }

    }

    private void loginUser(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (!FindAgentSystem.login(line, "Client")) {
                if (FindAgentSystem.findUser(line)) {
                    socket.send(new CommandContainer(AnswerCode.CLIENT_ONLINE_YET, "Server"));
                    return;
                }
                socket.send(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_CLIENT, "Server"));
                return;
            }
            Client user = new Client(line, socket, false);
            FindAgentSystem.addUser(user);
            log.info("Login client", user);
            client = user;
            socket.send(new CommandContainer(line, false, "goodLogin"));
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }

    private void loginAgent(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (!FindAgentSystem.login(line, "Agent")) {
                if (FindAgentSystem.findAgent(line)) {
                    socket.send(new CommandContainer(AnswerCode.AGENT_ONLINE_YET, "Server"));
                    return;
                }
                socket.send(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_AGENT, "Server"));
                return;
            }
            Client agent = new Client(line, socket, true);
            FindAgentSystem.addAgent(agent);
            log.info("Login agent", agent);
            socket.send(new CommandContainer(line, true, "goodLogin"));
            if (FindAgentSystem.findSystem(agent)) {
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }

    private void register(String command) {
        if (registerOrLoginAgentPatter.matcher(command).find()) regAgent(command);
        else if (registerOrLoginClientPatter.matcher(command).find()) regUser(command);
        else {
            socket.send(new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER, "Server"));
            log.warn("unknown type of user", command);
        }
    }//регистрация

    private void regAgent(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command.toString()).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (FindAgentSystem.findAgent(line)) {
                socket.send(new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));
                return;
            }
            Client agent = new Client(line, socket, true);
            FindAgentSystem.addAgent(agent);
            agent.getMysocket().send(new CommandContainer(line, true, "goodRegister"));
            log.info("register new agent", agent);
            if (FindAgentSystem.findSystem(agent)) {
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }//регистрация агента

    private void regUser(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command.toString()).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (FindAgentSystem.findUser(line)) {
                socket.send(new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));
                return;
            }
            Client user = new Client(line, socket, false);
            FindAgentSystem.addUser(user);
            log.info("register new client", user);
            client = user;
            socket.send(new CommandContainer(line, false, "goodRegister"));
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }//регистрация клиента

    protected void updateBufferedMessage() {
        if (bufferedMessage != null) {
            for (int i = 0; i < bufferedMessage.size(); i++)
                client.getRecipient().getMysocket().send(bufferedMessage.get(i));
            bufferedMessage = null;
        }
    }

    protected void waitAgent() {
        waitAgent = true;
    }//для отелючения обработки только сообщений(не команд) при отсутствии агента

    protected void notWaitAgent() {
        waitAgent = false;
    }

    public void leave() {
        if (client != null) {
            if (client.isAgent()) {
                if (client.getRecipient() != null) {
                    if (FindAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getMysocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE, "Server"));
                        client.getRecipient().getMysocket().updateBufferedMessage();
                        log.info("start conversation" + client.getRecipient().toString() + " " + client.getRecipient().getRecipient().toString());
                    } else {
                        client.getRecipient().getMysocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE_WAIT_NEW, "Server"));
                        client.getRecipient().setRecipient(null);
                    }
                }
                FindAgentSystem.removeAgent(client);
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer(AnswerCode.CLIENT_LEAVE, "Server"));
                    if (FindAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getRecipient().getMysocket().updateBufferedMessage();
                        log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
                    } else {
                        client.getRecipient().setRecipient(null);
                    }
                }
                FindAgentSystem.removeUser(client);
            }
            log.info("Client abort connection " + client.toString());
            client.setRecipient(null);
        } else log.info("Client abort connection unknown client");
    }//процесс корректного закрытия
}
