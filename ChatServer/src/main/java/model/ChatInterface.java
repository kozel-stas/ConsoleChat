package model;

public interface ChatInterface {
    void send(CommandContainer commandContainer);

    void close();

    void notWaitAgent();

    void waitAgent();

    void updateBufferedMessage();
}
