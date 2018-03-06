package com.touchsoft;

import com.google.gson.Gson;
import java.io.*;
import java.net.Socket;
import java.util.*;

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
               Thread demonlistener = new Thread(new InputListener(connect, this));
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

class InputListener implements Runnable {
     private  Map<AnswerCode,String> serverAnswer=null;
     private Socket socket;
     private ClientConnect connect;
     private Gson json;

     public InputListener(Socket socket, ClientConnect connect) {
          this.socket = socket;
          this.connect = connect;
          this.json = new Gson();
          serverAnswer =new EnumMap<AnswerCode, String>(AnswerCode.class);
          serverAnswer.put(AnswerCode.NEED_REGISTER_OR_LOGIN,"Вы должны авторизироваться или зарегистрироваться");
          serverAnswer.put(AnswerCode.UNKNOWN_MISTAKE,"Непредвиденная ошибка");
          serverAnswer.put(AnswerCode.UNKNOWN_COMMAND,"Неверная команда");
          serverAnswer.put(AnswerCode.DONT_HAVE_CHAT,"У вас нет активной беседы");
          serverAnswer.put(AnswerCode.LEAVE_CHAT,"Вы покинули беседу");
          serverAnswer.put(AnswerCode.CAN_NOT_LEAVE_AGENT_WITH_CLIENT,"Нельзя отключаться агентам с клиентом в сети");
          serverAnswer.put(AnswerCode.NO_AGENT_WAIT,"К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат");
          serverAnswer.put(AnswerCode.FIRST_AGENT_ANSWER_YOU,"Первый освободившийся агент ответит вам");
          serverAnswer.put(AnswerCode.DONT_HAVE_CLIENT,"У вас нет подключенных клиентов");
          serverAnswer.put(AnswerCode.UNKNOWN_TYPE_USER,"Неверно введен тип пользователя");
          serverAnswer.put(AnswerCode.INVALID_CHARACTERS,"Недопустимые символы в имени");
          serverAnswer.put(AnswerCode.CLIENT_ONLINE_YET,"Клиент с таким именем уже в сети");
          serverAnswer.put(AnswerCode.DONT_HAVE_REGISTER_CLIENT,"Нет такого зарегистрированного клиента");
          serverAnswer.put(AnswerCode.AGENT_ONLINE_YET,"Агент с таким именем уже в сети");
          serverAnswer.put(AnswerCode.DONT_HAVE_REGISTER_AGENT,"Нет такого зарегистрированного агента");
          serverAnswer.put(AnswerCode.NAME_ALREADY_USED,"Выбранное имя уже занято");
          serverAnswer.put(AnswerCode.CLIENT_LEAVE,"Клиент отключился");
          serverAnswer.put(AnswerCode.AGENT_LEAVE,"Агент отключился");
          serverAnswer.put(AnswerCode.AGENT_LEAVE_WAIT_NEW,"Агент отключился, первый освободившийся агент ответит вам");
          serverAnswer.put(AnswerCode.NEW_AGENT,"К вам подключился агент ");
          serverAnswer.put(AnswerCode.NEW_CLIENT,"Вы подключены к клиенту ");
          serverAnswer.put(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Вы уже зарегистрировались или авторизовались");
     }

     public void run() {
          try {
               BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
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
