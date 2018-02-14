package com.touchsoft;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private int port;
    private ServerSocket server;

    public ChatServer (int port) throws IOException{
        this.port=port;
        server=new ServerSocket(port);
    }

    public ChatServer () throws IOException{
        this.port=80;
        server=new ServerSocket(this.port);
    }

    public void run() throws IOException{
        while (true) {
            Socket connect = server.accept();
            if (connect!=null){
                SocketHandler client = new SocketHandler(connect);
                Thread thread = new Thread(client);
                thread.start();
            }
        }
    }
}

class SocketHandler implements Runnable {
    private Socket connect;
    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Controller controler;

    public SocketHandler (Socket connect) throws IOException {
        this.connect=connect;
        input = new ObjectInputStream(connect.getInputStream());
        output =new ObjectOutputStream(connect.getOutputStream());
    }

    public void setConnect(Socket connect) throws IOException  {
        this.connect = connect;
        input = new ObjectInputStream(connect.getInputStream());
        output =new ObjectOutputStream(connect.getOutputStream());
    }

    public void run()  {
        if (!connect.isClosed()){
            controler = new Controller();
            try {
                CommandContainer command = (CommandContainer) input.readObject();
                CommandContainer answer = controler.handler(command);
            }
            catch(IOException ex){
                close();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void close(){
        if(!connect.isClosed()){
            try {
                connect.close();
            }
            catch (IOException ex){
                ex.printStackTrace();
            }
        }
    }

    protected void send(CommandContainer container){
        if (!connect.isClosed()){
            try {
                output.writeObject(container);
            } catch (IOException ex){
                ex.printStackTrace();
            }

        }
    }
}
