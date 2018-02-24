package com.touchsoft;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.*;
import java.net.Socket;


public class SocketHandler implements Runnable {
    private Logger log= LoggerFactory.getLogger(com.touchsoft.SocketHandler.class);
    private Socket connect;
    private BufferedReader input;
    private BufferedWriter output;
    private Controller controller;
    private Gson json;

    public SocketHandler (Socket connect) throws IOException {
        this.connect=connect;
        input = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
        output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
        controller = new Controller(this);
        json =new Gson();
    }
    //прием сообщений и корректное закрытие и синхронизированный send;
    public void run()  {
        CommandContainer command=null;
        while (!connect.isClosed()){
            try {
                command = json.fromJson(input.readLine(),CommandContainer.class);
                if(command==null) {close(); break;}
                else controller.handler(command);
            }
            catch(IOException ex) {
                close();
                log.warn("Error reading command",ex);
            }
        }
    }

    public void waitAgent(){
        controller.waitAgent();
    }

    public void notWaitAgent(){
        controller.notWaitAgent();
    }

    public void  updateBufferedMessage(){
        controller.updateBufferedMessage();
    }

    private void close(){
        if(!connect.isClosed()){
            try {
                controller.leave();
                connect.close();
            }
            catch (IOException ex){
                log.error("Error closing connection",ex);
            }
        }
    }

    synchronized public void send(CommandContainer container){
        if (!connect.isClosed()){
            try {
                output.write(json.toJson(container));
                output.write("\n");
                output.flush();
            } catch (IOException ex){
                close();
                log.error("Error sending message",ex);
            }
        }
    }
}