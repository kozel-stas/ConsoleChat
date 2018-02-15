package com.touchsoft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class ClientConect {
     private String host;
     private int port;
     private Socket connect;
     protected static Client client=null;
     protected static boolean isAgent=false;

     public ClientConect(){
          this.host="localhost";
          this.port=8080;
     }

     public ClientConect(String host,int port){
          this.host=host;
          this.port=port;
     }

     public void run() throws IOException{
          connect = new Socket(host,port);
          ObjectOutputStream output=new ObjectOutputStream(connect.getOutputStream());
          Thread demonlistener=new Thread(new inputListener(connect));
          demonlistener.setDaemon(true);
          demonlistener.start();
          Scanner in=new Scanner(System.in);
          String line;
          while (true){
               line=in.nextLine();
               if(line!=null && line.equals("")==false){
                    if(line.charAt(0)=='/')
                         output.writeObject(new CommandContainer(line));
                    else{
                         if(client!=null){
                              output.writeObject(new CommandContainer(client.getName(),isAgent,line));
                         }
                         else System.out.println("Зарегистрируйтесь или авторизируйтесь пожалуйста");
                    }
               }
          }
     }
}

class inputListener implements Runnable {
     private Socket connect;

     public inputListener(Socket connect) {
          this.connect = connect;
     }

     public void run() {
          try {
               ObjectInputStream input = new ObjectInputStream(connect.getInputStream());
               while (true) {
                    CommandContainer command = (CommandContainer) input.readObject();
                    control(command);
               }
          } catch (IOException exception) {
               exception.printStackTrace();
          } catch (ClassNotFoundException ex) {
               ex.printStackTrace();
          }
     }

     private void control(CommandContainer container) {
          if (container == null) return;
          if (container.getMessage() != null && container.getMessage().equals("good") == false) {
               if (container.isAgent()) System.out.print("Агент ");
               else System.out.print("Клиент ");
               System.out.println(container.getName() + ":   " + container.getMessage());
          } else {
               if (container.getMessage() != null && container.getMessage().equals("good")) {
                    ClientConect.isAgent = container.isAgent();
                    if (container.isAgent() == true) {
                         ClientConect.client = (Client) new Agent(container.getName());
                         System.out.println("Вы успешно зарегистрированы как агент " + container.getName());
                    } else {
                         ClientConect.client = (Client) new User(container.getName());
                         System.out.println("Вы успешно зарегистрированы как клиент " + container.getName());
                    }
               } else {
                    if (container.getServerinfo() != null && container.getServerinfo().equals("good") == false) {
                         System.out.println(container.getName() + ":   " + container.getServerinfo());
                    }
               }
          }
     }
}
