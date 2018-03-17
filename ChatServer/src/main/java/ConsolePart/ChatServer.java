package ConsolePart;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import model.AnswerCode;
import model.FindAgentSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ChatServer implements Runnable {
    private static Logger log = LoggerFactory.getLogger(ChatServer.class);
    private int port;
    private ServerSocket server;
    private boolean isClosed = false;
    private List<SocketHandler> setSocketHandler = new ArrayList();

    public ChatServer(int port) {
        this.port = port;
        try {
            server = new ServerSocket(port);
        } catch (IOException e) {
            log.error("Server isn't start", e);
        }
        log.info("Start server port=" + port);
    }

    public ChatServer() {
        this.port = 8080;
        try {
            server = new ServerSocket(this.port);
        } catch (IOException e) {
            log.error("Server isn't start", e);
        }
        log.info("Start server port=" + port);
    }

    @Override
    public void run() {
        log.info("Start listener");
        while (!isClosed) {
            Socket connect = null;
            try {
                connect = server.accept();
            } catch (IOException e) {
                log.error("IOException in accept connection", e);
            }
            if (connect != null) {
                SocketHandler client = new SocketHandler(connect);
                setSocketHandler.add(client);
                new Thread(client).start();
                log.info("new client connect", client);
            }
        }
    }

    public void close() {
        isClosed = true;
        for (SocketHandler socketHandler : setSocketHandler)
            socketHandler.close();
    }

}

