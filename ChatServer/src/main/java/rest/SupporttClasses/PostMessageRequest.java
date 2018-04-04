package rest.SupporttClasses;

import model.SupportClasses.Role;

public class PostMessageRequest {
    private String login;
    private Role role;
    private String msg;
    private int numberPage;
    private int sizePage;

    public PostMessageRequest(String login, Role role, String msg,int numberPage, int sizePage) {
        this.login = login;
        this.role = role;
        this.msg = msg;
        this.numberPage=numberPage;
        this.sizePage=sizePage;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getNumberPage() {
        return numberPage;
    }

    public void setNumberPage(int numberPage) {
        this.numberPage = numberPage;
    }

    public int getSizePage() {
        return sizePage;
    }

    public void setSizePage(int sizePage) {
        this.sizePage = sizePage;
    }
}
