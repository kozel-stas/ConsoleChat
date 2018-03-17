package WebPart.servlet;

import ConsolePart.ChatServer;
import model.AnswerCode;
import model.FindAgentSystem;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import java.io.*;
import java.util.EnumMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConfigurationServletListener implements ServletContextListener {
    private static Logger log = LoggerFactory.getLogger(ChatServer.class);
    private FindAgentSystem findAgentSystem;
    private ChatServer chatServer;
    private final int numberPort = 8080;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        findAgentSystem=FindAgentSystem.getInstance();
        findAgentSystem.createDatabase();
        new Thread(chatServer = new ChatServer(numberPort)).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        chatServer.close();
        findAgentSystem.dropDatabase();
    }

}
