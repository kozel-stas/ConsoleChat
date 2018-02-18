package com.touchsoft;

import java.util.Date;
//можно не реализовывать классы юзера и агента, однако для расширяемости можно сделать
public class Client {
    private String name;
    private Client recipient;
    private SocketHandler mysocket;
    protected Client(){}

    public Client(String name,SocketHandler mysocket){
        this.name=name;
        this.mysocket=mysocket;
    }

    public SocketHandler getMysocket() {
        return mysocket;
    }

    public Client getRecipient() {
        return recipient;
    }

    public void setRecipient(Client recipient) {
        this.recipient = recipient;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return new String(name);
    }
}

class User extends Client{
    private Date date =new Date();
    private long last_activity;
    private long timeout=300000;

    public User (String name,SocketHandler mysocket){
        super(name,mysocket);
        last_activity=date.getTime();
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

    public Agent (String name,SocketHandler mysocket){
        super(name,mysocket);
        number_of_task=0;
    }

    public int getNumber_of_task() {
        return number_of_task;
    }

    public void iteration_number_of_task (){
        number_of_task++;
    }
}
