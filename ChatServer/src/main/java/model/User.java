package model;

import com.google.gson.annotations.Expose;
import model.SupportClasses.ChatInterface;
import model.SupportClasses.Role;
import model.SupportClasses.TypeApp;

import java.util.ArrayList;
import java.util.List;

public class User {
    @Expose
    private String login;
    @Expose
    private Role role;
    @Expose
    private TypeApp typeApp;
    private List<Chat> chat = new ArrayList();
    private ChatInterface socket;
    private static int maxClient;


    static {
        maxClient = 10;
    }


    private User() {
    }

    public User(String login, ChatInterface socket, Role role, TypeApp typeApp) {
        this.role = role;
        this.login = login;
        this.login.intern();
        this.socket = socket;
        this.typeApp = typeApp;
        if (role == Role.CLIENT) chat.add(new Chat(this));
    }

    public ChatInterface getSocket() {
        return socket;
    }

    public void setSocket(ChatInterface chatInterface) {
        socket = chatInterface;
    }

    public Chat getChat() {
        if (chat.size() > 0) return chat.get(0);
        else return null;
    }

    public Chat getChat(String login) {
        for (Chat chat : this.chat)
            if (login.equals(chat.getClient().getLogin()))
                return chat;
        return null;
    }

    public List getChats() {
        return chat;
    }

    public void addChat(Chat chat) {
        if (chat == null) return;
        if (checkMaxSize()) this.chat.add(chat);
        else if (typeApp == TypeApp.CONSOLE) {
            this.chat.clear();
            this.chat.add(0, chat);
        }

    }

    public void delChat(Chat chat) {
        this.chat.remove(chat);
    }

    public String getLogin() {
        return login;
    }

    public boolean checkMaxSize() {
        if (role == Role.AGENT && chat.size() < maxClient && (typeApp == TypeApp.WEB || typeApp == TypeApp.REST))
            return true;
        return false;
    }

    public void setTypeApp(TypeApp typeApp) {
        this.typeApp = typeApp;
    }

    public TypeApp getTypeApp() {
        return typeApp;
    }

    public void leave() {
        if (role == Role.AGENT) {
            FindAgentSystem.getInstance().remove(this);
            for (Chat chat : this.chat) {
                chat.agentLeave();
            }
        } else if (role == Role.CLIENT) {
            if (chat.size() == 1) chat.get(0).destroyChat();
        }
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Login " + login + ", role: " + role + ", typeApp: " + typeApp;
    }

}
