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
                socket.send(new CommandContainer("Вы должны авторизироваться или зарегистрироваться", "Server"));
            }
        } else {
            if (container.getCommand() != null) {
                handlerCommand(container);
            } else {
                if (container.getMessage() != null) {
                    handlerMessage(container);
                } else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer("Непредвиденная ошибка", "server"));
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
                        socket.send(new CommandContainer("Неверная команда", "server"));
                    }
                }
            } else socket.send(new CommandContainer("Вы уже зарегистрировались или авторизовались","server"));
        } else {
            line = command.substring(1, command.length());
            if (line.equals("leave")) {
                if (client != null && client.isAgent() && client.getRecipient() != null) {
                    socket.send(new CommandContainer("Нельзя отключаться агентам с клиентом в сети", "server"));
                } else {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer("Вы покинули беседу", "server"));
                    } else {
                        if (client == null)
                            socket.send(new CommandContainer("Вы должны авторизироваться или зарегистрироваться", "server"));
                        else socket.send(new CommandContainer("У вас нет активной беседы", "server"));
                    }
                }
            } else {
                if (line.equals("exit")) {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer("exit", "server"));
                    } else socket.send(new CommandContainer("exit", "server"));
                } else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer("Неверная команда", "server"));
                }
            }
        }
    }//обработчик команд

    private void handlerMessage(CommandContainer container) {
        if (client != null) {
            if (client.getRecipient() != null) {
                client.getRecipient().getMysocket().send(container);
                socket.send( new CommandContainer("good", "server"));
            } else {
                if (client.isAgent()) socket.send( new CommandContainer("У вас нет подключенных клиентов", "server"));
                else {
                    if (waitAgent == true) {
                        if(bufferedMessage==null) bufferedMessage=new ArrayList();
                        bufferedMessage.add(container);
                        socket.send(new CommandContainer("Первый освободившийся агент ответит вам","server"));
                    } else {
                        if (findAgentSystem.findSystem(client) == true) {
                            log.info("start conversation " + client.toString() + " " + client.getRecipient().toString());
                            client.getRecipient().getMysocket().send(container);
                            socket.send( new CommandContainer("good", "server"));
                        } else {
                            bufferedMessage = new ArrayList();
                            bufferedMessage.add(container);
                            socket.send( new CommandContainer("К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат", "server"));
                        }
                    }
                }
            }
        } else socket.send( new CommandContainer("Непредвиденная ошибка", "server"));
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
                    socket.send( new CommandContainer("Неверно введен тип пользователя", "server"));
                    log.warn("unknown type of user",line);
                }
            }
        } else socket.send( new CommandContainer("Неверная команда", "server"));
    }

    private void loginUser(int mark,StringBuilder command){
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (!findAgentSystem.login(line,"Client")){
                if(findAgentSystem.findUser(line)) {socket.send( new CommandContainer("Клиент с таким именем уже в сети", "server")); return;}
                socket.send( new CommandContainer("Нет такого зарегистрированного клиента", "server"));
                return;
            }
            Client user = new Client(line, socket,false);
            findAgentSystem.addUser(user);
            log.info("Login client",user);
            client = user;
            socket.send( new CommandContainer(line, false, "goodLogin"));
        } else {
            socket.send( new CommandContainer("Недопустимые символы в имени", "server"));
        }
    }

    private void loginAgent(int mark,StringBuilder command){
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (!findAgentSystem.login(line,"Agent")){
                if(findAgentSystem.findAgent(line)) {socket.send( new CommandContainer("Клиент с таким именем уже в сети", "server")); return;}
                socket.send( new CommandContainer("Нет такого зарегистрированного агента", "server"));
                return;
            }
            Client agent = new Client(line, socket,true);
            findAgentSystem.addAgent(agent);
            log.info("Login agent",agent);
            socket.send( new CommandContainer(line, true, "goodLogin"));
            if(findAgentSystem.findSystem(agent)==true){
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else {
            socket.send( new CommandContainer("Недопустимые символы в имени", "server"));
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
                    socket.send( new CommandContainer("Неверно введен тип пользователя", "server"));
                    log.warn("unknown type of user",line);
                }
            }
        } else socket.send( new CommandContainer("Неверная команда", "server"));
    }//регистрация

    private void regAgent(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (findAgentSystem.findAgent(line)) {socket.send(new CommandContainer("Выбранное имя уже занято", "server"));return;}
            Client agent = new Client(line, socket, true);
            findAgentSystem.addAgent(agent);
            agent.getMysocket().send(new CommandContainer(line, true, "good"));
            log.info("register new agent", agent);
            if(findAgentSystem.findSystem(agent)==true){
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else {
            socket.send(new CommandContainer("Недопустимые символы в имени", "server"));
        }
    }//регистрация агента

    private void regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (findAgentSystem.findUser(line)){ socket.send( new CommandContainer("Выбранное имя уже занято", "server"));return;}
            Client user = new Client(line, socket,false);
            findAgentSystem.addUser(user);
            log.info("register new client",user);
            client = user;
            socket.send( new CommandContainer(line, false, "good"));
        } else {
            socket.send( new CommandContainer("Недопустимые символы в имени", "server"));
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
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));
                        client.getRecipient().getMysocket().updateBufferedMessage();
                        log.info("start conversation" + client.getRecipient().toString() + " " + client.getRecipient().getRecipient().toString());
                    } else {
                        client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился, первый освободившийся агент ответит вам ", "server"));
                        client.getRecipient().setRecipient(null);
                    }
                }
                findAgentSystem.removeAgent(client);
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer("Клиент отключился", "server"));
                    if(findAgentSystem.findSystem(client.getRecipient())==true){
                        client.getRecipient().getRecipient().getMysocket().updateBufferedMessage();
                        log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
                    } else {
                        client.getRecipient().setRecipient(null);
                    }
                }
                findAgentSystem.removeUser(client);
            }
            log.info("Client abort connection " + client.toString());
            client.setRecipient(null);
        } else log.info("Client abort connection unknown client");
    }//процесс корректного закрытия
}
