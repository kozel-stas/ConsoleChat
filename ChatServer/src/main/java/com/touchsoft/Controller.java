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
            line = command.substring(1, mark);
            if (line.equals("register")) {
                register(command.substring(mark + 1, command.length()));
            } else {
                if (line.equals("authorization")) {
                    socket.send(new CommandContainer("Команда в разработке", "server"));// сделать если буду реализовывать сохранение агентов
                } else {
                    log.warn("unknown command " + container.toString());
                    socket.send(new CommandContainer("Неверная команда", "server"));
                }
            }
        } else {
            line = command.substring(1, command.length());
            if (line.equals("leave")) {
<<<<<<< HEAD
                if(client !=null && client.isAgent() && client.getRecipient()!=null){
                    socket.send(new CommandContainer("Нельзя отключаться агентам с клиентом в сети","server"));
                } else {
                    if(client!=null && client.getRecipient()!=null) {
                        leave();
                        socket.send(new CommandContainer("Вы покинули беседу", "server"));
                    }else socket.send(new CommandContainer("У вас нет активной беседы","server"));
                }
            } else {
                if (line.equals("exit")) {
                    if(client!=null && client.isAgent() && client.getRecipient()!=null){
                        socket.send( new CommandContainer("Нельзя отключаться агентам с клиентом в сети","server"));
                    } else {
                        if(client!=null && client.getRecipient()!=null) {
                            leave();
                            socket.send( new CommandContainer("exit", "server"));
                        }else socket.send( new CommandContainer("exit", "server"));
                    }
=======
                if (client != null && client.isAgent() && client.getRecipient() != null) {
                    socket.send(new CommandContainer("Нельзя отключаться агентам с клиентом в сети", "server"));
                } else {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer("Вы покинули беседу", "server"));
                    } else socket.send(new CommandContainer("У вас нет активной беседы", "server"));
                }
            } else {
                if (line.equals("exit")) {
                    if (client != null && client.getRecipient() != null) {
                        leave();
                        socket.send(new CommandContainer("exit", "server"));
                    } else socket.send(new CommandContainer("exit", "server"));

>>>>>>> test
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
                if (container.isAgent()) socket.send( new CommandContainer("У вас нет подключенных клиентов", "server"));
                else {
                    if (waitAgent == true) {
                        bufferedMessage.add(container);
                        socket.send(new CommandContainer("Первый освободившийся агент ответит вам","server"));
                    } else {
                        if (findAgentSystem.findSystem(client) == true) {
<<<<<<< HEAD
                            log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
=======
                            log.info("start conversation " + client.toString() + " " + client.getRecipient().toString());
>>>>>>> test
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
<<<<<<< HEAD
=======
                    log.warn("unknown type of user",line);
>>>>>>> test
                }
            }
        } else socket.send( new CommandContainer("Неверная команда", "server"));
    }//регистрация

    private void regAgent(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
<<<<<<< HEAD
            if (findAgentSystem.findAgent(line)) socket.send(new CommandContainer("Выбранное имя уже занято", "server"));
            Client agent = new Client(line, socket, true);
            findAgentSystem.addAgent(agent);
            if(findAgentSystem.findSystem(agent)==true){
                agent.getMysocket().send(new CommandContainer(line, true, "good"));
                agent.getRecipient().getMysocket().send(new CommandContainer("К вам подключился агент " +agent.getName(), "server"));
                agent.getMysocket().send(new CommandContainer("Вы подключены к клиенту " + agent.getRecipient().getName(), "server"));
                agent.getRecipient().getMysocket().updateBufferedMessage();
            } else agent.getMysocket().send(new CommandContainer(line, true, "good"));
=======
            if (findAgentSystem.findAgent(line)) {socket.send(new CommandContainer("Выбранное имя уже занято", "server"));return;}
            Client agent = new Client(line, socket, true);
            findAgentSystem.addAgent(agent);
            agent.getMysocket().send(new CommandContainer(line, true, "good"));
            log.info("register new agent", agent);
            if(findAgentSystem.findSystem(agent)==true){
                agent.getRecipient().getMysocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
>>>>>>> test
            client = agent;
        } else {
            socket.send(new CommandContainer("Недопустисые символы в имени", "server"));
        }
    }//регистрация агента

    private void regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
<<<<<<< HEAD
            if (findAgentSystem.findUser(line)) socket.send( new CommandContainer("Выбранное имя уже занято", "server"));
=======
            if (findAgentSystem.findUser(line)){ socket.send( new CommandContainer("Выбранное имя уже занято", "server"));return;}
>>>>>>> test
            Client user = new Client(line, socket,false);
            findAgentSystem.addUser(user);
            log.info("register new client",user);
            client = user;
            socket.send( new CommandContainer(line, false, "good"));
        } else {
            socket.send( new CommandContainer("Недопустисые символы в имени", "server"));
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
<<<<<<< HEAD
                    client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getMysocket().updateBufferedMessage();
                    } else {
                        client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился, первый освободившийся агент ответит вам ", "server"));
=======
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));
                        client.getRecipient().getMysocket().updateBufferedMessage();
                        log.info("start conversation" + client.getRecipient().toString() + " " + client.getRecipient().getRecipient().toString());
                    } else {
                        client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился, первый освободившийся агент ответит вам ", "server"));
                        bufferedMessage=new ArrayList<>();
>>>>>>> test
                        client.getRecipient().setRecipient(null);
                    }
                }
                findAgentSystem.removeAgent(client);
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer("Клиент отключился", "server"));
                    if(findAgentSystem.findSystem(client.getRecipient())==true){
                        client.getRecipient().getRecipient().getMysocket().updateBufferedMessage();
<<<<<<< HEAD
=======
                        log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
>>>>>>> test
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
