package model;

import ConsolePart.SocketHandler;
import java.util.ArrayList;
import java.util.List;

public class Client {
    private String login;
    private Role role;
    private TypeApp typeApp;

    private static int maxClient;

    private List <Client> recipient=new ArrayList();
    private ChatInterface socket;

    static {
        maxClient=10;
    }


    private Client() {
    }

    public Client(String login, SocketHandler mysocket, Role role, TypeApp typeApp ) {
        this.role=role;
        this.login = login;
        this.mysocket = mysocket;
        this.typeApp=typeApp;
    }

    public ChatInterface getSocket() {
        return socket;
    }

    public void setSocket(ChatInterface chatInterface) {
        socket = chatInterface;
    }

    public Client getRecipient() {
        if(recipient.size()>0) return recipient.get(0);
        else return null;
    }

    public Client getRecipient(String name){
        for(Client client:this.recipient)
            if(client.getLogin().equals(name))
                return client;
        return null;
    }

    public List getRecipients(){
        return recipient;
    }

    public void addRecipient(Client receipt){
        if(receipt!=null) {
            if(checkMaxSize()) recipient.add(receipt);
            else if(typeApp==TypeApp.CONSOLE) {
                recipient.remove(0);
                recipient.add(0,receipt);
            }
        }
    }

    public void deleteReceipt(Client receipt){
        this.recipient.remove(receipt);
    }

    public String getLogin() {
        return login;
    }

    public boolean checkMaxSize(){
        if(role==Role.AGENT && recipient.size()<maxClient && typeApp==TypeApp.WEB)
            return true;
        return false;
    }

    public void setTypeApp(TypeApp typeApp) {
        this.typeApp = typeApp;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Login "+login+", role: "+role+", typeApp: "+typeApp;
    }

}
