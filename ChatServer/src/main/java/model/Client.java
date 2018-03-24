package model;

import ConsolePart.SocketHandler;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String name;
    private List <Client> recipient=new ArrayList();
    private ChatInterface mysocket;
    private TypeApp typeApp;
    private boolean isAgent = false;
    private int maxClient=10;


    private Client() {
    }

    public Client(String name) {
        this.name = name;
    }

    public Client(String name, SocketHandler mysocket, boolean isAgent,TypeApp typeApp ) {
        this.isAgent = isAgent;
        this.name = name;
        this.mysocket = mysocket;
        this.typeApp=typeApp;
    }

    public ChatInterface getSocket() {
        return mysocket;
    }

    public void setSocket(ChatInterface chatInterface) {
        mysocket = chatInterface;
    }

    public boolean isAgent() {
        return isAgent;
    }

    public Client getRecipient() {
        if(recipient.size()>0) return recipient.get(0);
        else return null;
    }

    public void setRecipient(Client recipient) {
        if(this.recipient.size()>0) this.recipient.remove(0);
        if(recipient!=null) this.recipient.add(recipient);
    }

    public Client getReceiptByName(String name){
        for(Client client:this.recipient)
            if(client.getName().equals(name))
                return client;
        return null;
    }

    public void addReceipt(Client receipt){
        if(isAgent && this.recipient.size()<maxClient)
            this.recipient.add(0,receipt);

    }

    public void deleteReceipt(Client receipt){
        this.recipient.remove(receipt);
    }

    public List getRecipients(){
        return recipient;
    }

    public String getName() {
        return name;
    }

    public boolean checkMaxSize(){
        if(isAgent && recipient.size()<maxClient && typeApp==TypeApp.WEB)
            return true;
        return false;
    }

    @Override
    public String toString() {
        return new String(name) + " isAgent = " + isAgent;
    }

    public TypeApp getTypeApp() {
        return typeApp;
    }

    public void setTypeApp(TypeApp typeApp) {
        this.typeApp = typeApp;
    }
}
