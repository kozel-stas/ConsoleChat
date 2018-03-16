package ConsolePart;

import model.AnswerCode;
import model.Client;
import model.CommandContainer;
import model.FindAgentSystem;
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
    private FindAgentSystem findAgentSystem;

    private static Pattern registerOrLoginAgentOrClientPattern = Pattern.compile("^\\/(register|login) (agent|client) [A-z0-9]*$");
    private static Pattern registerPattern = Pattern.compile("^\\/register ");
    private static Pattern loginPattern = Pattern.compile("^\\/login ");
    private static Pattern leavePattern = Pattern.compile("^\\/leave$");
    private static Pattern exitPattern = Pattern.compile("^\\/exit$");
    private static Pattern registerOrLoginClientPatter = Pattern.compile("^\\/(register|login) client ");
    private static Pattern registerOrLoginAgentPatter = Pattern.compile("^\\/(register|login) agent ");

    public Controller(SocketHandler socket) {
        this.socket = socket;
        findAgentSystem=FindAgentSystem.getInstance();
    }

    public void handler(CommandContainer container) {
        log.info("request " + container.toString());
        if (client == null) {
            if (container.getCommand() != null) {
                handlerCommand(container);
                return;
            } else {
                socket.send(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN, "Server"));
            }
        } else {
            if (container.getCommand() != null) {
                handlerCommand(container);
                return;
            } else {
                if (container.getMessage() != null) {
                    handlerMessage(container);
                    return;
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
            client.getRecipient().getSocket().send(container);
        } else {
            if (client.isAgent()) socket.send(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT, "Server"));
            else {
                if (waitAgent) {
                    if (bufferedMessage == null) bufferedMessage = new ArrayList<>();
                    bufferedMessage.add(container);
                    socket.send(new CommandContainer(AnswerCode.FIRST_AGENT_ANSWER_YOU, "Server"));
                } else {
                    if (findAgentSystem.findSystem(client)) {
                        log.info("start conversation " + client.toString() + " " + client.getRecipient().toString());
                        client.getRecipient().getSocket().send(container);
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
            if (!findAgentSystem.authorize(line, "Client")) {
                if (findAgentSystem.findClient(line)) {
                    socket.send(new CommandContainer(AnswerCode.CLIENT_ONLINE_YET, "Server"));
                    return;
                }
                socket.send(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_CLIENT, "Server"));
                return;
            }
            Client user = new Client(line, socket, false);
            findAgentSystem.addClient(user);
            log.info("Login client", user);
            client = user;
            socket.send(new CommandContainer(line, false, "goodLogin"));
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }

    private void loginAgent(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (!findAgentSystem.authorize(line, "Agent")) {
                if (findAgentSystem.findAgent(line)) {
                    socket.send(new CommandContainer(AnswerCode.AGENT_ONLINE_YET, "Server"));
                    return;
                }
                socket.send(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_AGENT, "Server"));
                return;
            }
            Client agent = new Client(line, socket, true);
            findAgentSystem.addAgent(agent);
            log.info("Login agent", agent);
            socket.send(new CommandContainer(line, true, "goodLogin"));
            if (findAgentSystem.findSystem(agent)) {
                agent.getRecipient().getSocket().updateBufferedMessage();
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
            if (findAgentSystem.findAgent(line)) {
                socket.send(new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));
                return;
            }
            Client agent = new Client(line, socket, true);
            findAgentSystem.addAgent(agent);
            agent.getSocket().send(new CommandContainer(line, true, "goodRegister"));
            log.info("register new agent", agent);
            if (findAgentSystem.findSystem(agent)) {
                agent.getRecipient().getSocket().updateBufferedMessage();
                log.info("start conversation" + agent.toString() + " " + agent.getRecipient().toString());
            }
            client = agent;
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }//регистрация агента

    private void regUser(String command) {
        if (registerOrLoginAgentOrClientPattern.matcher(command.toString()).find()) {
            String line = command.substring(command.lastIndexOf(" ") + 1, command.length());
            if (findAgentSystem.findClient(line)) {
                socket.send(new CommandContainer(AnswerCode.NAME_ALREADY_USED, "Server"));
                return;
            }
            Client user = new Client(line, socket, false);
            findAgentSystem.addClient(user);
            log.info("register new client", user);
            client = user;
            socket.send(new CommandContainer(line, false, "goodRegister"));
        } else socket.send(new CommandContainer(AnswerCode.INVALID_CHARACTERS, "Server"));
    }//регистрация клиента

    protected void updateBufferedMessage() {
        if (bufferedMessage != null) {
            for (int i = 0; i < bufferedMessage.size(); i++)
                client.getRecipient().getSocket().send(bufferedMessage.get(i));
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
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE, "Server"));
                        client.getRecipient().getSocket().updateBufferedMessage();
                        log.info("start conversation" + client.getRecipient().toString() + " " + client.getRecipient().getRecipient().toString());
                    } else {
                        client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.AGENT_LEAVE_WAIT_NEW, "Server"));
                        client.getRecipient().setRecipient(null);
                    }
                }
                findAgentSystem.removeAgent(client);
            } else {
                if (client.getRecipient() != null) {
                    client.getRecipient().getSocket().send(new CommandContainer(AnswerCode.CLIENT_LEAVE, "Server"));
                    if (findAgentSystem.findSystem(client.getRecipient())) {
                        client.getRecipient().getRecipient().getSocket().updateBufferedMessage();
                        log.info("start conversation" + client.toString() + " " + client.getRecipient().toString());
                    } else {
                        client.getRecipient().setRecipient(null);
                    }
                }
                findAgentSystem.removeClient(client);
            }
            log.info("Client abort connection " + client.toString());
            client.setRecipient(null);
        } else log.info("Client abort connection unknown client");
    }//процесс корректного закрытия
}
