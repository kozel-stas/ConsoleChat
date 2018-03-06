package com.touchsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

//класс, отвечающий за обработку сообщений.
public class Controller {
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private Client client = null;
    private SocketHandler socket;
    private boolean waitAgent=false;
    private ArrayList<CommandContainer> bufferedMessage=null;

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
    }//основной обработчик

    private void handlerCommand(CommandContainer container) {
        int mark = -1;
        StringBuilder command = new StringBuilder(container.getCommand());
        mark = command.indexOf(" ");
        String line;
        if (mark != -1) {
            if(client==null) {
                line = command.substring(1, mark);
                if (line.equals("register")) {
                    register(command.substring(mark + 1, command.length()));
                } else {
                    if (line.equals("login")) {
                        login(command.substring(mark + 1, command.length()));
                    } else {
                        log.warn("unknown command " + container.toString());
                        socket.send(new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
                    }
                }
            } else socket.send(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server"));
        } else {
            line = command.substring(1, command.length());
            if (line.equals("leave")) {
                if (client != null && client.isAgent() && client.getRecipient() != null) {
                    socket.send(new CommandContainer(AnswerCode.CAN_NOT_LEAVE_AGENT_WITH_CLIENT, "Server"));
                } else {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer(AnswerCode.LEAVE_CHAT, "Server"));
                    } else {
                        if (client == null)
                            socket.send(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN, "Server"));
                        else socket.send(new CommandContainer(AnswerCode.DONT_HAVE_CHAT, "Server"));
                    }
                }
            } else {
                if (line.equals("exit")) {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer(AnswerCode.EXIT, "Server"));
                    } else socket.send(new CommandContainer(AnswerCode.EXIT, "Server"));
                } else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
                }
            }
        }
    }//обработчик команд

    private void handlerMessage(CommandContainer container) {
        if (client.getRecipient() != null) {
            client.getRecipient().getMysocket().send(container);
        } else {
            if (client.isAgent()) socket.send(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT, "Server"));
            else {
                if (waitAgent == true) {
                    if (bufferedMessage == null) bufferedMessage = new ArrayList();
                    bufferedMessage.add(container);
                    socket.send(new CommandContainer(AnswerCode.FIRST_AGENT_ANSWER_YOU, "Server"));
                } else {
                    if (FindAgentSystem.findSystem(client) == true) {
                        log.info("start conversation " + client.toString() + " " + client.getRecipient().toString());
                        client.getRecipient().getMysocket().send(container);
                    } else {
                        bufferedMessage = new ArrayList();
                        bufferedMessage.add(container);
                        socket.send(new CommandContainer(AnswerCode.NO_AGENT_WAIT, "Server"));
                    }
                }
            }
        }
    }//сообщений

    private void login(String line){
        StringBuilder command = new StringBuilder(line);
        int mark = command.indexOf(" ");
        if(mark!=-1) {
            if (command.substring(0, mark).equals("agent")) {
                loginAgent(mark,command);
            } else {
                if (command.substring(0, mark).equals("client")) {
                    loginUser(mark,command);
                } else {
                    socket.send( new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER, "Server"));
                    log.warn("unknown type of user",line);
                }
            }
        } else socket.send( new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
    }

    private void loginUser(int mark,StringBuilder command){
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (!FindAgentSystem.login(line,"Client")){
                if(FindAgentSystem.findUser(line)) {socket.send( new CommandContainer( AnswerCode.CLIENT_ONLINE_YET,"Server")); return;}
                socket.send( new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_CLIENT, "Server"));
                return;
            }
            Client user = new Client(line, socket,false);
            FindAgentSystem.addUser(user);
            log.info("Login client",user);
            client = user;
            socket.send( new CommandContainer(line, false, "goodLogin"));
        } else {
            socket.send( new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
        }
    }

    private void loginAgent(int mark,StringBuilder command){
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (!FindAgentSystem.login(line,"Agent")){
                if(FindAgentSystem.findAgent(line)) {socket.send( new CommandContainer(AnswerCode.AGENT_ONLINE_YET, "Server")); return;}
                socket.send( new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_AGENT, "Server"));
                return;
            }
            Client agent = new Client(line, socket,true);
            FindAgentSystem.addAgent(agent);
            log.info("Login agent",agent);
            socket.send( new CommandContainer(line, true, "goodLogin"));
            if(FindAgentSystem.findSystem(agent)){
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else {
            socket.send( new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
        }
    }

    private void register(String line) {
        StringBuilder command = new StringBuilder(line);
        int mark = command.indexOf(" ");
        if(mark!=-1) {
            if (command.substring(0, mark).equals("agent")) {
                regAgent(mark, command);
            } else {
                if (command.substring(0, mark).equals("client")) {
                    regUser(mark, command);
                } else {
                    socket.send( new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER, "Server"));
                    log.warn("unknown type of user",line);
                }
            }
        } else socket.send( new CommandContainer(AnswerCode.UNKNOWN_COMMAND, "Server"));
    }//регистрация

    private void regAgent(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (FindAgentSystem.findAgent(line)) {socket.send(new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));return;}
            Client agent = new Client(line, socket, true);
            FindAgentSystem.addAgent(agent);
            agent.getMysocket().send(new CommandContainer(line, true, "goodRegister"));
            log.info("register new agent", agent);
            if(FindAgentSystem.findSystem(agent)){
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else {
            socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
        }
    }//регистрация агента

    private void regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (FindAgentSystem.findUser(line)){ socket.send( new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));return;}
            Client user = new Client(line, socket,false);
            FindAgentSystem.addUser(user);
            log.info("register new client",user);
            client = user;
            socket.send( new CommandContainer(line, false, "goodRegister"));
        } else {
            socket.send( new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
        }
    }//регистрация клиента

    protected void updateBufferedMessage(){
        if (bufferedMessage!=null){
            for(int i=0;i<bufferedMessage.size();i++)
                client.getRecipient().getMysocket().send(bufferedMessage.get(i));
            bufferedMessage=null;
        }
    }

    protected void waitAgent(){
        waitAgent=true;
    }//для отелючения обработки только сообщений(не команд) при отсутствии агента

    protected void notWaitAgent(){
        waitAgent=false;
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
                    if(FindAgentSystem.findSystem(client.getRecipient())){
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
