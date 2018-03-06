package com.touchsoft;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.*;

public class ClientConnect {
     private Logger log = LoggerFactory.getLogger(ClientConnect.class);
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
               log.error("Exception with close",ex);
               ex.printStackTrace();
          } finally {
               System.exit(0);
          }
     }

     public void run() {
          try {
               log.info("Connect port "+port+" host "+host);
               connect = new Socket(host, port);
               log.info("Connect success");
               BufferedWriter output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
               Thread demonListener = new Thread(new InputListener(connect, this));
               demonListener.setDaemon(true);
               demonListener.start();
               log.info("Start demonListener");
               Scanner in = new Scanner(System.in);
               String line;
               System.out.println("=========================================\n                Command                \n /register agent|client \"name\"\n /login agent|client \"name\"\n /exit\n /leave");
               while (!connect.isClosed()) {
                    line = in.nextLine();
                    log.debug("New Line "+line);
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
               log.error("Server doesn't answer",ex);
               System.out.println("Сервер не в сети, попробуйте позже.");
          }
     }
}

class InputListener implements Runnable {
     private Logger log = LoggerFactory.getLogger(InputListener.class);
     private  Map<AnswerCode,String> serverAnswer=null;
     private Socket socket;
     private ClientConnect connect;
     private Gson json;

     public InputListener(Socket socket, ClientConnect connect) {
          this.socket = socket;
          this.connect = connect;
          this.json = new Gson();
          config();
     }

     private void config() {
          final String PATH="Client/src/main/resources/config.txt";
          serverAnswer = new EnumMap(AnswerCode.class);
          try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH))))) {
               String line;
               line = fileReader.readLine();
               while (line != null && !"".equals(line)) {
                    serverAnswer.put(AnswerCode.getEnumByInt(Integer.valueOf(line.substring(0, line.indexOf('|')))), line.substring(line.indexOf('|') + 1, line.length()));
                    line = fileReader.readLine();
               }
          } catch (FileNotFoundException e) {
               System.out.println("Непредвиденная ошибка");
               log.error("File not found",e);
               connect.exit();
          } catch (IOException e) {
               System.out.println("Непредвиденная ошибка");
               log.error("IOException",e);
               connect.exit();
          }
     }

     public void run() {
          try {
               BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
               while (!socket.isClosed()) {
                    CommandContainer command = json.fromJson(input.readLine(), CommandContainer.class);
                    control(command);
               }
          } catch (IOException exception) {
               log.error("IOException",exception);
               System.out.println("Сервер не в сети, поробуйте позже.");
               connect.exit();
          }
     }

     private void control(CommandContainer container) {
          log.info(container.toString());
          if (container == null) return;
          if(container.getServerinfo()!=AnswerCode.MESSAGE){
               if(container.getServerinfo()==AnswerCode.EXIT) {
                    connect.exit();
                    return;
               }
               if(container.getServerinfo()==AnswerCode.NEW_AGENT || container.getServerinfo()==AnswerCode.NEW_CLIENT) {
                    System.out.println(serverAnswer.get(container.getServerinfo())+container.getName());
                    return;
               }
               System.out.println(container.getName()+"     "+serverAnswer.get(container.getServerinfo()));
          }else {
               if (container.getMessage() != null && !container.getMessage().equals("goodRegister") && !container.getMessage().equals("goodLogin")) {
                    if (container.isAgent()) System.out.print("Агент ");
                    else System.out.print("Клиент ");
                    System.out.println(container.getName() + ":   " + container.getMessage());
               } else {
                    if (container.getMessage() != null && container.getMessage().equals("goodRegister")) {
                         ClientConnect.client = new Client(container.getName(), container.isAgent());
                         if (container.isAgent()) {
                              System.out.println("Вы успешно зарегистрированы как агент " + container.getName());
                         } else {
                              System.out.println("Вы успешно зарегистрированы как клиент " + container.getName());
                         }
                    } else {
                         if (container.getMessage() != null && container.getMessage().equals("goodLogin")) {
                              ClientConnect.client = new Client(container.getName(), container.isAgent());
                              if (container.isAgent()) {
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
