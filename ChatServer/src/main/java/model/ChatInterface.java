package model;

public interface ChatInterface {

    void send(CommandContainer commandContainer);

    void close();
}
