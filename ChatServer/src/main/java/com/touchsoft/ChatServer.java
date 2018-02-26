package com.touchsoft;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ChatServer {
    private Logger log=LoggerFactory.getLogger(ChatServer.class);
    private int port;
    private ServerSocket server;
    private ArrayList<java.lang.String> serverAnswer;
    public ChatServer (int port) throws IOException{
        this.port=port;
        server=new ServerSocket(port);
        log.info("Start server port="+port);
        answerArray();
    }

    public ChatServer () throws IOException{
        this.port=8080;
        server=new ServerSocket(this.port);
        log.info("Start server port="+port);
        answerArray();
    }

    public void run() throws IOException{
        //ловит клиентов
        log.info("Start listener");
        findAgentSystem.createDatabase();
        ThreadPoolExecutor executor =new ThreadPoolExecutor(512,512,10, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(512));
        while (true) {
            Socket connect = server.accept();
            if (connect!=null){
                SocketHandler client = new SocketHandler(connect,serverAnswer);
                executor.execute(client);
//                Thread thread = new Thread(client);
//                thread.start();
                log.info("new client connect",client);
            }
        }
    }

    private void answerArray(){
        serverAnswer =new ArrayList();
        serverAnswer.add(0,"Вы должны авторизироваться или зарегистрироваться");
        serverAnswer.add(1,"Непредвиденная ошибка");
        serverAnswer.add(2,"Неверная команда");
        serverAnswer.add(3,"У вас нет активной беседы");
        serverAnswer.add(4,"Вы покинули беседу");
        serverAnswer.add(5,"Нельзя отключаться агентам с клиентом в сети");
        serverAnswer.add(6,"К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат");
        serverAnswer.add(7,"Первый освободившийся агент ответит вам");
        serverAnswer.add(8,"У вас нет подключенных клиентов");
        serverAnswer.add(9,"Неверно введен тип пользователя");
        serverAnswer.add(10,"Недопустимые символы в имени");
        serverAnswer.add(11,"Клиент с таким именем уже в сети");
        serverAnswer.add(12,"Нет такого зарегистрированного клиента");
        serverAnswer.add(13,"Агент с таким именем уже в сети");
        serverAnswer.add(14,"Нет такого зарегистрированного агента");
        serverAnswer.add(15,"Выбранное имя уже занято");
        serverAnswer.add(16,"Клиент отключился");
        serverAnswer.add(17,"Агент отключился");
        serverAnswer.add(18,"Агент отключился, первый освободившийся агент ответит вам");
        serverAnswer.add(19,"К вам подключился агент ");
        serverAnswer.add(20,"Вы подключены к клиенту ");
        serverAnswer.add(21,"Вы уже зарегистрировались или авторизовались");
    }
}

