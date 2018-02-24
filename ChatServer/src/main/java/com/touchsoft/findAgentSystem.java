package com.touchsoft;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
// класс реализующий память и потстоянное расперделение агентов и клиентов
// Статическое для меньшего количества синхронизированных методов
public class findAgentSystem {
    private static  final String url = "jdbc:derby:memory:ServerChatDB";
    private static Connection connection;
    private static Statement stmt;
    private static ConcurrentLinkedQueue<Client> waitAgents = new ConcurrentLinkedQueue<>();
    private static ConcurrentLinkedQueue<Client> waitUsers=new ConcurrentLinkedQueue<>();
    private static CopyOnWriteArrayList<Client> users =new CopyOnWriteArrayList();
    private static CopyOnWriteArrayList<Client> agents =new CopyOnWriteArrayList();

    public static void createDatabase(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection=DriverManager.getConnection(url+";create=true");
            String createAgentTable="CREATE TABLE Agent (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1), name VARCHAR(100) NOT NULL)";
            stmt=connection.createStatement();
            stmt.executeUpdate(createAgentTable);
            String createClientTable="CREATE TABLE Client (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1), name VARCHAR(100) NOT NULL)";
            stmt.executeUpdate(createClientTable);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void dropDatabase(){
        try {
            connection=DriverManager.getConnection(url+";drop=true");
        } catch (SQLException e) {

        }
    }

    public static boolean login(String name,String type){
        if (findInDatabase(name,type)){
            StringBuilder stringBuilder=new StringBuilder("DELETE FROM ");
            stringBuilder.append(type);
            stringBuilder.append(" WHERE name='");
            stringBuilder.append(name);
            stringBuilder.append("'");
            try {
                stmt.executeUpdate(stringBuilder.toString());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }else {
            return false;
        }
    }

    private static void addInDatabase(Client client,String type){
        StringBuilder stringBuilder=new StringBuilder("INSERT INTO ");
        stringBuilder.append(type);
        stringBuilder.append("(name) VALUES ('");
        stringBuilder.append(client.getName());
        stringBuilder.append("')");
        try {
            stmt.executeUpdate(stringBuilder.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean findInDatabase(String name,String type){
        StringBuilder stringBuilder=new StringBuilder("SELECT name FROM ");
        stringBuilder.append(type);
        stringBuilder.append(" WHERE name='");
        stringBuilder.append(name);
        stringBuilder.append("'");
        try {
            ResultSet rst=stmt.executeQuery(stringBuilder.toString());
            if(rst.next()) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
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
            if(((Client)iterator.next()).getName().equals(name)) return true;
        }
        return findInDatabase(name,"Client");
    }

    public static boolean findAgent (String name){
        Iterator iterator =agents.iterator();
        while (iterator.hasNext()){
            if(((Client)iterator.next()).getName().equals(name))return true;
        }
        return findInDatabase(name,"Agent");
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
        addInDatabase(agent,"Agent");
    }

    public static void removeUser(Client user){
        waitUsers.remove(user);
        users.remove(user);
        addInDatabase(user,"Client");
    }


}
