package com.touchsoft;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
        //ThreadPoolExecutor executor =new ThreadPoolExecutor(3,512,10, TimeUnit.MILLISECONDS,new ArrayBlockingQueue<Runnable>(512));
        while (true) {
            Socket connect = server.accept();
            if (connect!=null){
                SocketHandler client = new SocketHandler(connect);
                // executor.execute(client);
                Thread thread = new Thread(client);
                thread.start();
                log.info("Client connect",client);
            }
        }
    }
}

class SocketHandler implements Runnable {
    private Logger log=LoggerFactory.getLogger(SocketHandler.class);
    private Socket connect;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Controller controler;

    public SocketHandler (Socket connect) throws IOException {
        this.connect=connect;
        input = new ObjectInputStream(connect.getInputStream());
        output =new ObjectOutputStream(connect.getOutputStream());
        controler = new Controller(this);
    }
    //прием сообщений и корректное закрытие и синхронизированный send;
    public void run()  {
        while (!connect.isClosed()){
            try {
                CommandContainer command = (CommandContainer) input.readObject();
                controler.handler(command);
            }
            catch(IOException ex){
                close();
            } catch (ClassNotFoundException e) {
                log.error("Don't find class CommandContainer",e);
            }
        }
    }

    public void waitAgent(){
        controler.waitAgent();
    }

    public void notWaitAgent(){
        controler.notWaitAgent();
    }

    public void  updateBufferedMessage(){
        controler.updateBufferedMessage();
    }

    private void close(){
        if(!connect.isClosed()){
            try {
                controler.leave();
                connect.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    synchronized protected void send(CommandContainer container){
        if (!connect.isClosed()){
            try {
                output.writeObject(container);
            } catch (IOException ex){
                close();
                log.warn("Abort client");
            }

        }
    }
}
