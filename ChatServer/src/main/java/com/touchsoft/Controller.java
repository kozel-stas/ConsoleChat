package com.touchsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Controller {
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private Client client = null;
    private boolean isAgent = false;
    private SocketHandler socket;

    public Controller(SocketHandler socket) {
        this.socket = socket;
    }

    public CommandContainer handler(CommandContainer container) {
        log.info("request " + container.toString());
        if (client == null) {
            if (container.getCommand() != null) {
                return handlerCommand(container);
            } else {
                return new CommandContainer("Вы должны авторизироваться или зарегистрироваться", "Server");
            }
        } else {
            if (container.getCommand() != null) {
                return handlerCommand(container);
            } else {
                if (container.getMessage() != null) {
                    return handlerMessage(container);
                } else {
                    log.warn("unknown command " + container.toString());
                    return new CommandContainer("Непредвиденная ошибка", "server");
                }
            }
        }
    }

    private CommandContainer handlerCommand(CommandContainer container) {
        int mark = -1;
        StringBuilder command = new StringBuilder(container.getCommand());
        mark = command.indexOf(" ");
        String line;
        if (mark != -1) {
            line = command.substring(1, mark);
            if (line.equals("register")) {
                return register(command.substring(mark + 1, command.length()));
            } else {
                if (line.equals("authorization")) {
                    return new CommandContainer("Команда в разработке", "server");// сделать если буду реализовывать сохранение агентов
                } else {
                    log.warn("unknown command " + container.toString());
                    return new CommandContainer("Неверная команда", "server");
                }
            }
        } else {
            line = command.substring(1, command.length());
            if (line.equals("leave")) {
                return new CommandContainer("leave", "server");
            } else {
                if (line.equals("exit")) {
                    return new CommandContainer("exit", "server");
                } else {
                    log.warn("unknown command " + container.toString());
                    return new CommandContainer("Неверная команда", "server");
                }
            }
        }
    }

    private CommandContainer handlerMessage(CommandContainer container) {
        if (client != null) {
            if (client.getRecipient() != null) {
                client.getRecipient().getMysocket().send(container);
                return new CommandContainer("good", "server");
            } else {
                if (container.isAgent()) return new CommandContainer("У вас нет подключенных клиентов", "server");
                else {
                    log.info("start conversation"+client.toString()+" "+client.getRecipient().toString());
                    socket.getModule().addWaitUser((User) client);
                    while (client.getRecipient() == null) ;
                    client.getRecipient().getMysocket().send(container);
                    return new CommandContainer("good", "server");
                }
            }
        } else return new CommandContainer("Непредвиденная ошибка", "server");
    }

    private CommandContainer register(String line) {
        StringBuilder command = new StringBuilder(line);
        int mark = command.indexOf(" ");
        if (command.substring(0, mark).equals("agent")) {
            return regAgent(mark, command);
        } else {
            if (command.substring(0, mark).equals("client")) {
                return regUser(mark, command);
            } else {
                return new CommandContainer("Неверно введен тип пользователя", "server");
            }
        }
    }

    private CommandContainer regAgent(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (socket.getModule().findAgent(line)) return new CommandContainer("Выбранное имя уже занято", "server");
            Agent agent = new Agent(line, socket);
            socket.getModule().addAgent(agent);
            socket.getModule().addWaitAgent(agent);
            client = agent;
            isAgent = true;
            return new CommandContainer(line, true, "good");
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }

    private CommandContainer regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (socket.getModule().findUser(line)) return new CommandContainer("Выбранное имя уже занято", "server");
            User user = new User(line, socket);
            socket.getModule().addUser(user);
            client = user;
            return new CommandContainer(line, false, "good");
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }

    public void leave() {
        if (client != null) {
            if (isAgent) {
                if (client.getRecipient() != null)
                    client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));//ркоенект нового агента
                client.getRecipient().setRecipient(null);
                socket.getModule().removeAgent((Agent) client);
                socket.getModule().addWaitUser((User) client.getRecipient());
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer("Клиент отключился", "server"));
                    client.getRecipient().setRecipient(null);
                    ((Agent) client.getRecipient()).iteration_number_of_task();
                    socket.getModule().addWaitAgent((Agent) client.getRecipient());
                }
                socket.getModule().removeUser((User) client);
            }
            if (client.getRecipient() != null) {
                client.getRecipient().setRecipient(null);
                log.info("Client abort connection " + client.toString());
            }
            client.setRecipient(null);
            log.info("Client abort connection unknown client");
            client = null;
        }
    }

}
