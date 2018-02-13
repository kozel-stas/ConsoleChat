package com.touchsoft;

import java.util.Date;

public class Client {
    private String name;
    private boolean status; //реализовтаь при сохранении агентов и юзеров

    protected Client(){}

    public Client(String name){
        this.name=name;
    }

    public String getName() {
        return name;
    }
}

class User extends Client{
    private Agent agent=null;
    private Date date =new Date();
    private long last_activity;
    private long timeout=300000;

    public User (String name){
        super(name);
        last_activity=date.getTime();
    }

    public Agent getAgent() {
        return agent;
    }

    public void setAgent(Agent agent) {
        this.agent = agent;
    }

    public boolean checkTimeout (){
        if (last_activity+timeout<date.getTime()) return true;
        else return false;
    }

    public void updateTimeout (){
        last_activity=date.getTime();
    }

}

class Agent extends Client{
    private int number_of_task;
    private User user=null;

    public Agent (String name){
        super(name);
        number_of_task=0;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber_of_task() {
        return number_of_task;
    }

    public void iteration_number_of_task (){
        number_of_task++;
    }
}
