package ConsolePart;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;
import java.util.regex.Pattern;

public class ClientConnect {
    private static Pattern registerOrLoginAgentOrClientPattern = Pattern.compile("^\\/(register|login) (agent|client) [A-z0-9]*$");
    private static Pattern registerPattern = Pattern.compile("^\\/register ");
    private static Pattern loginPattern = Pattern.compile("^\\/login ");
    private static Pattern leavePattern = Pattern.compile("^\\/leave$");
    private static Pattern exitPattern = Pattern.compile("^\\/exit$");
    private static Pattern registerOrLoginClientPatter = Pattern.compile("^\\/(register|login) client ");
    private static Pattern registerOrLoginAgentPatter = Pattern.compile("^\\/(register|login) agent ");
    private static Logger log = LoggerFactory.getLogger(ClientConnect.class);
    private Map<AnswerCode, String> serverAnswer = null;
    private String host;
    private int port;
    private Socket connect;
    private Gson json;
    protected static User user = null;

    public ClientConnect() {
        this.host = "localhost";
        this.port = 8080;
        json = new Gson();
        config();
    }

    public ClientConnect(String host, int port) {
        this.host = host;
        this.port = port;
        json = new Gson();
        config();
    }

    private void config() {
        final String PATH = "Client/src/main/resources/config.txt";
        serverAnswer = new EnumMap(AnswerCode.class);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH))))) {
            String line;
            line = fileReader.readLine();
            while (line != null && !"".equals(line)) {
                serverAnswer.put(AnswerCode.getEnumByInt(Integer.valueOf(line.substring(0, line.indexOf('|')))), line.substring(line.indexOf('|') + 1, line.length()));
                line = fileReader.readLine();
            }
        } catch (FileNotFoundException e) {
            System.out.println("Непредвиденная ошибка");
            log.error("File not found", e);
            exit();
        } catch (IOException e) {
            System.out.println("Непредвиденная ошибка");
            log.error("IOException", e);
            exit();
        }
    }

    public void exit() {
        try {
            connect.close();
        } catch (IOException ex) {
            log.error("Exception with close", ex);
            ex.printStackTrace();
        } finally {
            System.exit(0);
        }
    }

    public void run() {
        try {
            log.info("Connect port " + port + " host " + host);
            connect = new Socket(host, port);
            log.info("Connect success");
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
            Thread demonListener = new Thread(new InputListener(connect, this, serverAnswer));
            demonListener.setDaemon(true);
            demonListener.start();
            log.info("Start demonListener");
            Scanner in = new Scanner(System.in);
            String line;
            System.out.println("=========================================\n                Command                \n /register agent|user \"name\"\n /login agent|user \"name\"\n /exit\n /leave");
            while (!connect.isClosed()) {
                line = in.nextLine();
                log.debug("New Line " + line);
                if (line != null && !line.equals("")) {
                    if (line.charAt(0) == '/') {
                        CommandContainer commandContainer = handler(line);
                        if (commandContainer != null) {
                            output.write(json.toJson(commandContainer));
                            output.write("\n");
                            output.flush();
                        }
                    } else {
                        if (user != null) {
                            output.write(json.toJson(new CommandContainer(user.getLogin(), user.getRole(), line)));
                            output.write("\n");
                            output.flush();
                        } else System.out.println("Зарегистрируйтесь или авторизируйтесь пожалуйста");
                    }
                }
            }
        } catch (IOException ex) {
            log.error("Server doesn't answer", ex);
            System.out.println("Сервер не в сети, попробуйте позже.");
        }
    }

    private CommandContainer handler(String line) {
        if (leavePattern.matcher(line).find())
            return new CommandContainer(AnswerCode.LEAVE_CHAT, user == null ? null : user.getLogin(), user == null ? null : user.getRole());
        if (exitPattern.matcher(line).find())
            return new CommandContainer(AnswerCode.EXIT, user == null ? null : user.getLogin(), user == null ? null : user.getRole());
        if (registerPattern.matcher(line).find()) {
            if (registerOrLoginClientPatter.matcher(line).find()) {
                if (registerOrLoginAgentOrClientPattern.matcher(line).find()) {
                    String name = line.substring(line.lastIndexOf(" ") + 1, line.length());
                    return new CommandContainer(AnswerCode.REGISTER, name, Role.CLIENT);
                } else {
                    System.out.println("Server     " + serverAnswer.get(AnswerCode.INVALID_CHARACTERS));
                    return null;
                }
            } else if (registerOrLoginAgentPatter.matcher(line).find()) {
                if (registerOrLoginAgentOrClientPattern.matcher(line).find()) {
                    String name = line.substring(line.lastIndexOf(" ") + 1, line.length());
                    return new CommandContainer(AnswerCode.REGISTER, name, Role.AGENT);
                } else {
                    System.out.println("Server     " + serverAnswer.get(AnswerCode.INVALID_CHARACTERS));
                    return null;
                }
            } else {
                System.out.println("Server     " + serverAnswer.get(AnswerCode.UNKNOWN_TYPE_USER));
                return null;
            }
        }
        if (loginPattern.matcher(line).find()) {
            if (registerOrLoginClientPatter.matcher(line).find()) {
                if (registerOrLoginAgentOrClientPattern.matcher(line).find()) {
                    String name = line.substring(line.lastIndexOf(" ") + 1, line.length());
                    return new CommandContainer(AnswerCode.LOGIN, name, Role.CLIENT);
                } else {
                    System.out.println("Server     " + serverAnswer.get(AnswerCode.INVALID_CHARACTERS));
                    return null;
                }
            } else if (registerOrLoginAgentPatter.matcher(line).find()) {
                if (registerOrLoginAgentOrClientPattern.matcher(line).find()) {
                    String name = line.substring(line.lastIndexOf(" ") + 1, line.length());
                    return new CommandContainer(AnswerCode.LOGIN, name, Role.AGENT);
                } else {
                    System.out.println("Server     " + serverAnswer.get(AnswerCode.INVALID_CHARACTERS));
                    return null;
                }
            } else {
                System.out.println("Server     " + serverAnswer.get(AnswerCode.UNKNOWN_TYPE_USER));
                return null;
            }
        }
        System.out.println("Server     " + serverAnswer.get(AnswerCode.UNKNOWN_COMMAND));
        return null;
    }
}

class InputListener implements Runnable {
    private static Logger log = LoggerFactory.getLogger(InputListener.class);
    private Map<AnswerCode, String> serverAnswer = null;
    private Socket socket;
    private ClientConnect connect;
    private Gson json;

    public InputListener(Socket socket, ClientConnect connect, Map<AnswerCode, String> serverAnswer) {
        this.socket = socket;
        this.connect = connect;
        this.json = new Gson();
        this.serverAnswer = serverAnswer;
    }

    public void run() {
        try {
            BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            while (!socket.isClosed()) {
                CommandContainer command = json.fromJson(input.readLine(), CommandContainer.class);
                control(command);
            }
        } catch (IOException exception) {
            log.error("IOException", exception);
            System.out.println("Сервер не в сети, поробуйте позже.");
            connect.exit();
        }
    }

    private void control(CommandContainer container) {
        log.info(container.toString());
        if (container == null) return;
        if (container.getCommand() != AnswerCode.MESSAGE) {
            if (container.getServerInfo() == AnswerCode.EXIT) {
                connect.exit();
                return;
            }
            if (container.getServerInfo() == AnswerCode.NEW_AGENT || container.getServerInfo() == AnswerCode.NEW_CLIENT) {
                System.out.println(serverAnswer.get(container.getServerInfo()) + " " + container.getLogin());
                return;
            }
            if (container.getServerInfo() == AnswerCode.GOOD_REGISTER) {
                ClientConnect.user = new User(container.getLogin(), container.getRole());
                if (container.getRole() == Role.AGENT) {
                    System.out.println("Вы успешно зарегистрированы как агент " + container.getLogin());
                } else {
                    System.out.println("Вы успешно зарегистрированы как клиент " + container.getLogin());
                }
                return;
            }
            if (container.getServerInfo() == AnswerCode.GOOD_LOGIN) {
                ClientConnect.user = new User(container.getLogin(), container.getRole());
                if (container.getRole() == Role.AGENT) {
                    System.out.println("Вы успешно авторизированы как агент " + container.getLogin());
                } else {
                    System.out.println("Вы успешно авторизированы как клиент " + container.getLogin());
                }
                return;
            }
            System.out.println(container.getLogin() + "     " + serverAnswer.get(container.getServerInfo()));
        } else {
            if (container.getMessage() != null) {
                if (container.getRole() == Role.AGENT) System.out.print("Агент ");
                else System.out.print("Клиент ");
                System.out.println(container.getLogin() + ":   " + container.getMessage());
            }
        }
    }

}
