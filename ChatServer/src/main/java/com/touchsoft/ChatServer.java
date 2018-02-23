package com.touchsoft;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.gson.*;

public class ChatServer {
    private Logger log=LoggerFactory.getLogger(ChatServer.class);
    private int port;
    private ServerSocket server;

    public ChatServer (int port) throws IOException{
        this.port=port;
        server=new ServerSocket(port);
        log.info("Start server port="+port);
    }

    public ChatServer () throws IOException{
        this.port=8080;
        server=new ServerSocket(this.port);
        log.info("Start server port="+port);
    }

    public void run() throws IOException{
        //ловит клиентов
        log.info("Start listener");
        ThreadPoolExecutor executor =new ThreadPoolExecutor(512,512,10, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(512));
        while (true) {
            Socket connect = server.accept();
            if (connect!=null){
                SocketHandler client = new SocketHandler(connect);
                executor.execute(client);
//                Thread thread = new Thread(client);
//                thread.start();
                log.info("new client connect",client);
            }
        }
    }
}

