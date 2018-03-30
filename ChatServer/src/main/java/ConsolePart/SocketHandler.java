package ConsolePart;

import com.google.gson.Gson;
import model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;


public class SocketHandler implements Runnable, ChatInterface {
    private static Logger log = LoggerFactory.getLogger(SocketHandler.class);
    private static DataManipulate dataManipulate;
    private static Gson json;
    private User user;
    private Socket connect;
    private BufferedReader input;
    private BufferedWriter output;

    static {
        json = new Gson();
        dataManipulate = DataManipulate.getInstance();
    }

    public SocketHandler(Socket connect) {
        this.connect = connect;
        try {
            input = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
            output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            log.error("IOException in start SocketHandler", e);
        }
    }

    public void run() {
        CommandContainer commandContainer;
        while (!connect.isClosed()) {
            try {
                commandContainer = json.fromJson(input.readLine(), CommandContainer.class);
                if (commandContainer == null) {
                    close();
                    break;
                } else handler(commandContainer);
            } catch (IOException ex) {
                close();
                log.warn("Error reading command", ex);
            }
        }
    }

    private void handler(CommandContainer commandContainer) {
        log.info("request " + commandContainer.toString());
        if (user == null) {
            if (commandContainer.getCommand() != null) {
                handlerCommand(commandContainer);
                return;
            } else {
                send(new CommandContainer("Server",null, AnswerCode.NEED_REGISTER_OR_LOGIN));
            }
        } else {
            if (commandContainer.getCommand() == AnswerCode.MESSAGE) {
                if(user.getChat()!=null) user.getChat().sendMessage(commandContainer);
                else send(new CommandContainer("Server",null,AnswerCode.DONT_HAVE_CLIENT));
                return;
            } else {
                if (commandContainer.getCommand() != null) {
                    handlerCommand(commandContainer);
                    return;
                } else {
                    log.warn("unknown command " + commandContainer.toString());
                    send(new CommandContainer("Server",null, AnswerCode.UNKNOWN_MISTAKE));
                }
            }
        }
    }

    private void handlerCommand(CommandContainer commandContainer) {
        AnswerCode command = commandContainer.getCommand();
        if (user == null) {
            if (command == AnswerCode.REGISTER) {
                User tempUser =new User(commandContainer.getLogin(),this,commandContainer.getRole(),TypeApp.CONSOLE);
                CommandContainer answer = dataManipulate.register(tempUser);
                if(answer.getServerInfo()==AnswerCode.GOOD_REGISTER) user=tempUser;
                else send(answer);
                return;
            } else {
                if (command == AnswerCode.LOGIN) {
                    User tempUser =new User(commandContainer.getLogin(),this,commandContainer.getRole(),TypeApp.CONSOLE);
                    CommandContainer answer = dataManipulate.login(tempUser);
                    if(answer.getServerInfo()==AnswerCode.GOOD_LOGIN) user=tempUser;
                    send(answer);
                    return;
                } else {
                    if (command == AnswerCode.LEAVE_CHAT)
                        send(new CommandContainer("Server",null, AnswerCode.NEED_REGISTER_OR_LOGIN));
                    else if (command == AnswerCode.EXIT) send(new CommandContainer("Server",null, AnswerCode.EXIT));
                    else {
                        log.warn("unknown command " + commandContainer.toString());
                        send(new CommandContainer("Server",null, AnswerCode.UNKNOWN_COMMAND));
                        return;
                    }
                }
            }
        } else if (command == AnswerCode.LEAVE_CHAT) {
            if (user.getRole() == Role.AGENT) send(new CommandContainer("Server",null, AnswerCode.CAN_NOT_LEAVE_AGENT));
            else if (user.getChat() != null) {
                user.leave();
                send(new CommandContainer("Server",null, AnswerCode.LEAVE_CHAT));
            } else send(new CommandContainer("Server",null, AnswerCode.DONT_HAVE_CHAT));
        } else {
            if (command == AnswerCode.EXIT) {
                send(new CommandContainer("Server",null, AnswerCode.EXIT));
                close();
            } else {
                if (command == AnswerCode.REGISTER || command == AnswerCode.LOGIN)
                    send(new CommandContainer("Server", null,AnswerCode.YOU_REGISTER_OR_LOGIN_YET));
                else {
                    log.warn("unknown command " + commandContainer.toString());
                    send(new CommandContainer("Server", null,AnswerCode.UNKNOWN_COMMAND));
                }
            }
        }
    }

    public void send(CommandContainer commandContainer) {
        String msg = json.toJson(commandContainer);
        if (!connect.isClosed()) {
            synchronized (this) {
                try {
                    output.write(msg);
                    output.write("\n");
                    output.flush();
                } catch (IOException ex) {
                    close();
                    log.error("Error sending message", ex);
                }
            }
        }
    }

    @Override
    public void close() {
        if (!connect.isClosed()) {
            try {
                user.leave();
                dataManipulate.remove(user);
                connect.close();
            } catch (IOException ex) {
                log.error("Error closing connection", ex);
            }
        }
    }

}