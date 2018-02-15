package com.touchsoft;

import java.util.concurrent.CopyOnWriteArrayList;

public class Controller {
    private static CopyOnWriteArrayList<Agent> agents = new CopyOnWriteArrayList();//массивы нужны тут, если реализовывать сохранение агентов и юзеров
    private static CopyOnWriteArrayList<User> users = new CopyOnWriteArrayList<User>();
    private Client client = null;
    private boolean isAgent = false;
    private SocketHandler socket;

    public Controller(SocketHandler socket) {
        this.socket = socket;
    }

    public CommandContainer handler(CommandContainer container) {
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
                } else return new CommandContainer("Непредвиденная ошибка", "server");
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
                    return new CommandContainer("Неверная команда", "server");
                }
            }
        } else {
            line=command.substring(1,command.length());
            if (line.equals("leave")) {
                return new CommandContainer("leave", "server");
            } else {
                if (line.equals("exit")) {
                    return new CommandContainer("exit", "server");
                } else return new CommandContainer("Неверная команда", "server");
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
                else return new CommandContainer("Непредвиденная ошибка", "server");
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
            if (command.lastIndexOf(" ") == mark) {
                String line = command.substring(mark + 1, command.length());
                for (int i = 0; i < agents.size(); i++) {
                    if (agents.get(i).getName().equals(line)) {
                        return new CommandContainer("Выбранное имя уже занято", "server");
                    }
                }
                Agent agent = new Agent(line, socket);
                agents.add(agent);
                client = agent;
                isAgent = true;
                return new CommandContainer(line, true, "good");
            } else {
                return new CommandContainer("Недопустисые символы в имени", "server");
            }
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }

    private CommandContainer regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getName().equals(line)) {
                    return new CommandContainer("Выбранное имя уже занято", "server");
                }
            }
            User user = new User(line, socket);
            findAgent(user);
            users.add(user);
            client = user;
            return new CommandContainer(line, false, "good");
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }

    private void findAgent(User user) {

        for (int i = 0; i < agents.size(); i++) {
            if (agents.get(i).getRecipient() == null) {
                user.setRecipient(agents.get(i));
                agents.get(i).setRecipient(user);
                agents.get(i).getMysocket().send(new CommandContainer("К вас подлючился клиент " + user.getName(), "server"));
                break;
            }
        }
    }

    public void close() {
        if (client != null) {
            if (isAgent) {
                if (client.getRecipient() != null)
                    client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));//ркоенект нового агента
                agents.remove(client);
            } else {
                if (client.getRecipient() != null)
                    client.getRecipient().getMysocket().send(new CommandContainer("Клиент отключился", "server"));
                ((Agent) client.getRecipient()).iteration_number_of_task();
                users.remove(client);
            }
            if (client.getRecipient() != null)
                client.getRecipient().setRecipient(null);
            client.setRecipient(null);
        }
    }

}
