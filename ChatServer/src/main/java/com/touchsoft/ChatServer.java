package com.touchsoft;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.EnumMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private Logger log = LoggerFactory.getLogger(ChatServer.class);
    private int port;
    private ServerSocket server;
    private Map<AnswerCode, String> serverAnswer;
    private final int numberOfPools = 100;

    public ChatServer(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
        log.info("Start server port=" + port);
        answerArray();
    }

    public ChatServer() throws IOException {
        this.port = 8080;
        server = new ServerSocket(this.port);
        log.info("Start server port=" + port);
        answerArray();
    }

    public void run() throws IOException {
        log.info("Start listener");
        FindAgentSystem.createDatabase();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numberOfPools, numberOfPools, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(512));
        while (true) {
            Socket connect = server.accept();
            if (connect != null) {
                SocketHandler client = new SocketHandler(connect, serverAnswer);
                executor.execute(client);
                log.info("new client connect", client);
            }
        }
    }

    private void answerArray() {
        serverAnswer = new EnumMap<AnswerCode, String>(AnswerCode.class);
        serverAnswer.put(AnswerCode.NEED_REGISTER_OR_LOGIN, "Вы должны авторизироваться или зарегистрироваться");
        serverAnswer.put(AnswerCode.UNKNOWN_MISTAKE, "Непредвиденная ошибка");
        serverAnswer.put(AnswerCode.UNKNOWN_COMMAND, "Неверная команда");
        serverAnswer.put(AnswerCode.DONT_HAVE_CHAT, "У вас нет активной беседы");
        serverAnswer.put(AnswerCode.LEAVE_CHAT, "Вы покинули беседу");
        serverAnswer.put(AnswerCode.CAN_NOT_LEAVE_AGENT_WITH_CLIENT, "Нельзя отключаться агентам с клиентом в сети");
        serverAnswer.put(AnswerCode.NO_AGENT_WAIT, "К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат");
        serverAnswer.put(AnswerCode.FIRST_AGENT_ANSWER_YOU, "Первый освободившийся агент ответит вам");
        serverAnswer.put(AnswerCode.DONT_HAVE_CLIENT, "У вас нет подключенных клиентов");
        serverAnswer.put(AnswerCode.UNKNOWN_TYPE_USER, "Неверно введен тип пользователя");
        serverAnswer.put(AnswerCode.INVALID_CHARACTERS, "Недопустимые символы в имени");
        serverAnswer.put(AnswerCode.CLIENT_ONLINE_YET, "Клиент с таким именем уже в сети");
        serverAnswer.put(AnswerCode.DONT_HAVE_REGISTER_CLIENT, "Нет такого зарегистрированного клиента");
        serverAnswer.put(AnswerCode.AGENT_ONLINE_YET, "Агент с таким именем уже в сети");
        serverAnswer.put(AnswerCode.DONT_HAVE_REGISTER_AGENT, "Нет такого зарегистрированного агента");
        serverAnswer.put(AnswerCode.NAME_ALREADY_USED, "Выбранное имя уже занято");
        serverAnswer.put(AnswerCode.CLIENT_LEAVE, "Клиент отключился");
        serverAnswer.put(AnswerCode.AGENT_LEAVE, "Агент отключился");
        serverAnswer.put(AnswerCode.AGENT_LEAVE_WAIT_NEW, "Агент отключился, первый освободившийся агент ответит вам");
        serverAnswer.put(AnswerCode.NEW_AGENT, "К вам подключился агент ");
        serverAnswer.put(AnswerCode.NEW_CLIENT, "Вы подключены к клиенту ");
        serverAnswer.put(AnswerCode.YOU_REGISTER_OR_LOGIN_YET, "Вы уже зарегистрировались или авторизовались");
    }
}

