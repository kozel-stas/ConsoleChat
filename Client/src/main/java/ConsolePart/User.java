package ConsolePart;

public class User {
    private String login;
    private Role role;

    private User() {
    }

    public User(String login, Role role) {
        this.role = role;
        this.login = login;
    }

    public String getLogin() {
        return login;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "Login " + login + ", role: " + role;
    }

}
