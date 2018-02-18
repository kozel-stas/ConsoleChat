package com.touchsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
//класс, отвечающий за обработку сообщений.
public class Controller {
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private Client client = null;
    private boolean isAgent = false;
    private SocketHandler socket;
    private boolean waitAgent=false;

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
                    if (waitAgent==true){
                        return new CommandContainer("Ожидайте, первый освободившийся агент к вам подключится","server");
                    } else return handlerMessage(container);
                } else {
                    log.warn("unknown command " + container.toString());
                    return new CommandContainer("Непредвиденная ошибка", "server");
                }
            }
        }
    }//основной обработчик

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
                if(isAgent && client.getRecipient()!=null){
                   return new CommandContainer("Нельзя отключаться агентам с клиентом в сети","server");
                } else {
                    if(client.getRecipient()!=null) {
                        leave();
                        return new CommandContainer("Вы покинули беседу", "server");
                    }else return new CommandContainer("У вас нет активной беседы","server");
                }
            } else {
                if (line.equals("exit")) {
                    if(isAgent && client.getRecipient()!=null){
                        return new CommandContainer("Нельзя отключаться агентам с клиентом в сети","server");
                    } else {
                        if(client.getRecipient()!=null) {
                            leave();
                            return new CommandContainer("exit", "server");
                        }else return new CommandContainer("exit", "server");
                    }
                } else {
                    log.warn("unknown command " + container.toString());
                    return new CommandContainer("Неверная команда", "server");
                }
            }
        }
    }//обработчик команд

    private CommandContainer handlerMessage(CommandContainer container) {
        if (client != null) {
            if (client.getRecipient() != null) {
                client.getRecipient().getMysocket().send(container);
                return new CommandContainer("good", "server");
            } else {
                if (container.isAgent()) return new CommandContainer("У вас нет подключенных клиентов", "server");
                else {
                    waitAgent=true;
                    findAgentSystem.addWaitUser((User) client);
                    try {
                        Thread.currentThread().sleep(20);//разница в работе потоков
                    } catch (InterruptedException ex){
                        Thread.currentThread().interrupt();
                    }
                    if(waitAgent==false) {
                        log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
                        client.getRecipient().getMysocket().send(container);
                        return new CommandContainer("good", "server");
                    } else {
                        return new CommandContainer("К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат","server");
                    }
                }
            }
        } else return new CommandContainer("Непредвиденная ошибка", "server");
    }//сообщений

    private CommandContainer register(String line) {
        StringBuilder command = new StringBuilder(line);
        int mark = command.indexOf(" ");
        if(mark!=-1) {
            if (command.substring(0, mark).equals("agent")) {
                return regAgent(mark, command);
            } else {
                if (command.substring(0, mark).equals("client")) {
                    return regUser(mark, command);
                } else {
                    return new CommandContainer("Неверно введен тип пользователя", "server");
                }
            }
        } else return new CommandContainer("Неверная команда", "server");
    }//регистрация

    private CommandContainer regAgent(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (findAgentSystem.findAgent(line)) return new CommandContainer("Выбранное имя уже занято", "server");
            Agent agent = new Agent(line, socket);
            findAgentSystem.addAgent(agent);
            findAgentSystem.addWaitAgent(agent);
            client = agent;
            isAgent = true;
            return new CommandContainer(line, true, "good");
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }//регистрация агента

    private CommandContainer regUser(int mark, StringBuilder command) {
        if (command.lastIndexOf(" ") == mark) {
            String line = command.substring(mark + 1, command.length());
            if (findAgentSystem.findUser(line)) return new CommandContainer("Выбранное имя уже занято", "server");
            User user = new User(line, socket);
            findAgentSystem.addUser(user);
            client = user;
            return new CommandContainer(line, false, "good");
        } else {
            return new CommandContainer("Недопустисые символы в имени", "server");
        }
    }//регистрация клиента

    public void updatewaitAgent(){
        waitAgent=false;
    }//для отелючения обработки только сообщений(не команд) при отсутствии агента

    public void leave() {
        if (client != null) {
            if (isAgent) {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer("Агент отключился", "server"));
                    findAgentSystem.addWaitUser((User) client.getRecipient());
                    client.getRecipient().setRecipient(null);
                }
                findAgentSystem.removeAgent((Agent) client);
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getMysocket().send(new CommandContainer("Клиент отключился", "server"));
                    findAgentSystem.addWaitAgent((Agent) client.getRecipient());
                    client.getRecipient().setRecipient(null);
                    ((Agent) client.getRecipient()).iteration_number_of_task();
                }
                findAgentSystem.removeUser((User) client);
            }
            log.info("Client abort connection " + client.toString());
            client.setRecipient(null);
        } else log.info("Client abort connection unknown client");
    }//процесс корректного закрытия

}
