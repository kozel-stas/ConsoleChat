package com.touchsoft;

import java.io.*;
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
    private int numberOfPools;

    public ChatServer(int port) throws IOException {
        this.port = port;
        server = new ServerSocket(port);
        log.info("Start server port=" + port);
        config();
    }

    public ChatServer() throws IOException {
        this.port = 8080;
        server = new ServerSocket(this.port);
        log.info("Start server port=" + port);
        config();
    }

    public void run() throws IOException {
        log.info("Start listener");
        FindAgentSystem.createDatabase();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(numberOfPools, numberOfPools, 10, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(512));
        while (true) {
            Socket connect = server.accept();
            if (connect != null) {
                SocketHandler client = new SocketHandler(connect);
                executor.execute(client);
                log.info("new client connect", client);
            }
        }
    }

    private void config() {
        final String PATH="ChatServer/src/main/resources/config.txt";
        serverAnswer = new EnumMap(AnswerCode.class);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH))))) {
            String line = fileReader.readLine();
            if (line != null)
                numberOfPools = Integer.valueOf(line);
            line = fileReader.readLine();
            while (line != null && !"".equals(line)) {
                serverAnswer.put(AnswerCode.getEnumByInt(Integer.valueOf(line.substring(0, line.indexOf('|')))), line.substring(line.indexOf('|') + 1, line.length()));
                line = fileReader.readLine();
            }
        } catch (FileNotFoundException e) {
            log.error("Config file isn't exist",e);
        } catch (IOException e) {
            log.error("Problem with input stream with config file",e);
        }
    }
}

