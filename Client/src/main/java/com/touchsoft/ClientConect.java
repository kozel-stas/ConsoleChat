package com.touchsoft;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.Timer;

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

     public void close () throws IOException {
          if (!connect.isClosed())
               connect.close();
     }

     public void exit () throws IOException{
          close();
          System.exit(0);
     }

     public void run() throws IOException{
          connect = new Socket(host,port);
          ObjectOutputStream output=new ObjectOutputStream(connect.getOutputStream());
          Thread demonlistener=new Thread(new inputListener(connect,this));
          demonlistener.setDaemon(true);
          demonlistener.start();
          Scanner in=new Scanner(System.in);
          String line;
          while (!connect.isClosed()){
               line=in.nextLine();
               if(line!=null && line.equals("")==false){
                    if(line.charAt(0)=='/') {
                         output.writeObject(new CommandContainer(line));
                         if(!isAgent && client!=null)
                              ((User) client).updateTimeout();
                    }
                    else{
                         if(client!=null){
                              output.writeObject(new CommandContainer(client.getName(),isAgent,line));
                              if(!isAgent)
                                   ((User) client).updateTimeout();
                         }
                         else System.out.println("Зарегистрируйтесь или авторизируйтесь пожалуйста");
                    }
               }
               if(line.equals("/leave")){ close(); break;}
               if(line.equals("/exit")) exit();
          }
     }
}

class inputListener implements Runnable {
     private Socket socket;
     private ClientConect connect;

     public inputListener(Socket socket, ClientConect conect) {
          this.socket = socket;
          this.connect = conect;

     }

     public void run() {
          try {
               ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
               while (!socket.isClosed()) {
                    CommandContainer command = (CommandContainer) input.readObject();
                    control(command);
               }
          } catch (IOException exception) {
               if(!socket.isClosed())
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
                         new Timer().schedule(new timeout((User) ClientConect.client),((User) ClientConect.client).getTimeout(),((User) ClientConect.client).getTimeout());
                    }
               } else {
                    if (container.getServerinfo() != null && container.getServerinfo().equals("good") == false) {
                         System.out.println(container.getName() + ":   " + container.getServerinfo());
                    }
               }
          }
     }
}
