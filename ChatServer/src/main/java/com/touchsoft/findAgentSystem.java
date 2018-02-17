package com.touchsoft;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class findAgentSystem implements Runnable {
    private  ArrayBlockingQueue<Agent> waitAgents = new ArrayBlockingQueue(512);
    private  ArrayBlockingQueue<User> waitUsers=new ArrayBlockingQueue<User>(512);
    private CopyOnWriteArrayList<User> users =new CopyOnWriteArrayList<User>();
    private CopyOnWriteArrayList<Agent> agents =new CopyOnWriteArrayList<Agent>();

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

    public boolean findUser (String name){
        for(int i=0;i<users.size();i++)
            if(users.get(i).getName().equals(name)) return true;
        return false;
    }

    public boolean findAgent (String name){
        for(int i=0;i<agents.size();i++)
            if(agents.get(i).getName().equals(name)) return true;
        return false;
    }

    public void addUser(User user){
        users.add(user);
    }

    public void addAgent(Agent agent){
        agents.add(agent);
    }

    public void addWaitUser(User user){
        try {
            waitUsers.put(user);
        }  catch (InterruptedException ex){
            ex.fillInStackTrace();
        }
    }

    public void addWaitAgent(Agent agent){
        try {
            waitAgents.put(agent);
        }  catch (InterruptedException ex){
            ex.fillInStackTrace();
        }
    }

    public void removeAgent(Agent agent){
        waitAgents.remove(agent);
        agents.remove(agent);
    }

    public void removeUser(User user){
        waitUsers.remove(user);
        users.remove(user);
    }
}
