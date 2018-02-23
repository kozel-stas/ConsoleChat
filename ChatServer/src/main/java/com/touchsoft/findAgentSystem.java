package com.touchsoft;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
// класс реализующий память и потстоянное расперделение агентов и клиентов
// Статическое для меньшего количества синхронизированных методов
public class findAgentSystem {
    private static  final String url = "jdbc:derby:memory:Mydb";
    private static String user = "root";
    private static String password = "root";
    private static Connection con;
    private static Statement stmt;
    private static ConcurrentLinkedQueue<Client> waitAgents = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<Client> waitUsers=new ConcurrentLinkedQueue<>();
    private static CopyOnWriteArrayList<Client> users =new CopyOnWriteArrayList();
    private static CopyOnWriteArrayList<Client> agents =new CopyOnWriteArrayList();
    public static void testconnect(){
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            con = DriverManager.getConnection(url, user, password);
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT name FROM agent");
            System.out.println(rs);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static synchronized boolean findSystem (Client client) {
        if (client.isAgent()) {
            if (waitUsers.size() > 0) {
                Client user = waitUsers.poll();
                user.setRecipient(client);
                client.setRecipient(user);
                user.getMysocket().notWaitAgent();
                user.getMysocket().send(new CommandContainer("К вам подключился агент " + client.getName(), "server"));
                client.getMysocket().send(new CommandContainer("Вы подключены к клиенту " + user.getName(), "server"));
                return true;
            } else {
                waitAgents.add(client);
                return false;
            }
        } else {
            if (waitAgents.size() > 0) {
                Client agent = waitAgents.poll();
                agent.setRecipient(client);
                client.setRecipient(agent);
                client.getMysocket().notWaitAgent();
                client.getMysocket().send(new CommandContainer("К вам подключился агент " + agent.getName(), "server"));
                agent.getMysocket().send(new CommandContainer("Вы подключены к клиенту " + client.getName(), "server"));
                return true;
            } else {
                client.getMysocket().waitAgent();
                waitUsers.add(client);
                return false;
            }
        }
    }

    public static boolean findUser (String name){
        Iterator iterator =users.iterator();
        while (iterator.hasNext()){
            if(((Client)iterator.next()).getName().equals(name))return true;
        }
        return false;
    }

    public static boolean findAgent (String name){
        Iterator iterator =agents.iterator();
        while (iterator.hasNext()){
            if(((Client)iterator.next()).getName().equals(name))return true;
        }
        return false;
    }

    public static void addUser(Client user){
        users.add(user);
    }

    public static void addAgent(Client agent){
        agents.add(agent);
    }

    public static void removeAgent(Client agent){
        waitAgents.remove(agent);
        agents.remove(agent);
    }

    public static void removeUser(Client user){
        waitUsers.remove(user);
        users.remove(user);
    }


}
