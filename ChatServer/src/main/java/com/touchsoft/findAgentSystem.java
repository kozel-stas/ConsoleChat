package com.touchsoft;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;
// класс реализующий память и потстоянное расперделение агентов и клиентов
// Статическое для меньшего количества синхронизированных методов
public class findAgentSystem implements Runnable {
    private static   ArrayBlockingQueue<Agent> waitAgents = new ArrayBlockingQueue(512);
    private static   ArrayBlockingQueue<User> waitUsers=new ArrayBlockingQueue<User>(512);
    private static CopyOnWriteArrayList<User> users =new CopyOnWriteArrayList<User>();
    private static CopyOnWriteArrayList<Agent> agents =new CopyOnWriteArrayList<Agent>();

    @Override
    public void run() {
        while (true){
            if(waitAgents.size()>0){
                if(waitUsers.size()>0){
                    try {
                        Agent agent = waitAgents.take();
                        User user = waitUsers.take();
                        user.setRecipient(agent);
                        agent.setRecipient(user);
                        user.getMysocket().send(new CommandContainer("К вам подключился агент", "server"));
                        user.getMysocket().updatewaitAgent();
                    } catch (InterruptedException ex){
                        ex.fillInStackTrace();
                    }
                }
            }
        }
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

    public static void addUser(User user){
        users.add(user);
    }

    public static void addAgent(Agent agent){
        agents.add(agent);
    }

    public static void addWaitUser(User user){
        try {
            waitUsers.put(user);
        }  catch (InterruptedException ex){
            ex.fillInStackTrace();
        }
    }

    public static void addWaitAgent(Agent agent){
        try {
            waitAgents.put(agent);
        }  catch (InterruptedException ex){
            ex.fillInStackTrace();
        }
    }

    public static void removeAgent(Agent agent){
        waitAgents.remove(agent);
        agents.remove(agent);
    }

    public static void removeUser(User user){
        waitUsers.remove(user);
        users.remove(user);
    }
}
