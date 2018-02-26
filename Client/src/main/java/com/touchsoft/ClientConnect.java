package com.touchsoft;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class ClientConnect {
     private String host;
     private int port;
     private Socket connect;
     private Gson json;
     protected static Client client = null;

     public ClientConnect() {
          this.host = "localhost";
          this.port = 8080;
          json = new Gson();
     }

     public ClientConnect(String host, int port) {
          this.host = host;
          this.port = port;
          json = new Gson();
     }

     public void exit(){
          try {
               connect.close();
          } catch (IOException ex){
               ex.printStackTrace();
          } finally {
               System.exit(0);
          }
     }

     public void run() {
          try {
               connect = new Socket(host, port);
               BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
               Thread demonlistener = new Thread(new inputListener(connect, this));
               demonlistener.setDaemon(true);
               demonlistener.start();
               Scanner in = new Scanner(System.in);
               String line;
               System.out.println("=========================================\n                Command                \n /register agent|client \"name\"\n /login agent|client \"name\"\n /exit\n /leave");
               while (!connect.isClosed()) {
                    line = in.nextLine();
                    if (line != null && line.equals("") == false) {
                         if (line.charAt(0) == '/') {
                              output.write(json.toJson(new CommandContainer(line)));
                              output.write("\n");
                              output.flush();
                         } else {
                              if (client != null) {
                                   output.write(json.toJson(new CommandContainer(client.getName(), client.isAgent(), line)));
                                   output.write("\n");
                                   output.flush();
                              } else System.out.println("Зарегистрируйтесь или авторизируйтесь пожалуйста");
                         }
                    }
               }
          } catch (IOException ex) {
               System.out.println("Сервер не в сети, попробуйте позже.");
          }
     }
}

class inputListener implements Runnable {
     private  ArrayList<String> serverAnswer=null;
     private Socket socket;
     private ClientConnect connect;
     private Gson json;

     public inputListener(Socket socket, ClientConnect connect) {
          this.socket = socket;
          this.connect = connect;
          this.json = new Gson();

     }

     public void run() {
          try {
               BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
               serverAnswer=json.fromJson(input.readLine(),ArrayList.class);
               if(serverAnswer==null){
                    System.out.println("Проблема на стороне сервера, попрубуйте еще раз");
                    connect.exit();
                    return;
               }
               while (!socket.isClosed()) {
                    CommandContainer command = json.fromJson(input.readLine(), CommandContainer.class);
                    control(command);
               }
          } catch (IOException exception) {
               System.out.println("Сервер не в сети, поробуйте позже.");
               connect.exit();
          }
     }

     private void control(CommandContainer container) {
          if (container == null) return;
          if(container.getServerinfo()!=-1){
               if(container.getServerinfo()==666) {connect.exit(); return;}
               if(container.getServerinfo()==19 || container.getServerinfo()==20) {System.out.println(serverAnswer.get(container.getServerinfo())+container.getName()); return;}
               System.out.println(container.getName()+"     "+serverAnswer.get(container.getServerinfo()));
          }else {
               if (container.getMessage() != null && container.getMessage().equals("goodRegister") == false && container.getMessage().equals("goodLogin") == false) {
                    if (container.isAgent()) System.out.print("Агент ");
                    else System.out.print("Клиент ");
                    System.out.println(container.getName() + ":   " + container.getMessage());
               } else {
                    if (container.getMessage() != null && container.getMessage().equals("goodRegister")) {
                         ClientConnect.client = new Client(container.getName(), container.isAgent());
                         if (container.isAgent() == true) {
                              System.out.println("Вы успешно зарегистрированы как агент " + container.getName());
                         } else {
                              System.out.println("Вы успешно зарегистрированы как клиент " + container.getName());
                         }
                    } else {
                         if (container.getMessage() != null && container.getMessage().equals("goodLogin")) {
                              ClientConnect.client = new Client(container.getName(), container.isAgent());
                              if (container.isAgent() == true) {
                                   System.out.println("Вы успешно авторизированы как агент " + container.getName());
                              } else {
                                   System.out.println("Вы успешно авторизированы как клиент " + container.getName());
                              }
                         }
                    }
               }
          }
     }
}
