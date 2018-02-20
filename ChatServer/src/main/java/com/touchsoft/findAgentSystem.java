package com.touchsoft;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
// класс реализующий память и потстоянное расперделение агентов и клиентов
// Статическое для меньшего количества синхронизированных методов
public class findAgentSystem {
    private static   ArrayBlockingQueue<Client> waitAgents = new ArrayBlockingQueue(512);
    private static   ArrayBlockingQueue<Client> waitUsers=new ArrayBlockingQueue(512);
    private static CopyOnWriteArrayList<Client> users =new CopyOnWriteArrayList();
    private static CopyOnWriteArrayList<Client> agents =new CopyOnWriteArrayList();

    public static synchronized boolean findSystem (Client client){
        try {
            if (client.isAgent()) {
                if (waitUsers.size() > 0) {
                    Client user = waitUsers.take();
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
                    Client agent = waitAgents.take();
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
        } catch (InterruptedException ex) {ex.printStackTrace(); return false;}
    }

    public static boolean findUser (String name){
        for(int i=0;i<users.size();i++)
            if(users.get(i).getName().equals(name)) return true;
        return false;
    }

    public static boolean findAgent (String name){
        for(int i=0;i<agents.size();i++)
            if(agents.get(i).getName().equals(name)) return true;
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
