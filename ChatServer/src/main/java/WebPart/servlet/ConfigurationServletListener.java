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
    private Map serverAnswer;
    private int numberPort = 8080;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
       // config();
        findAgentSystem=FindAgentSystem.getInstance();
        findAgentSystem.createDatabase();
        new Thread(chatServer = new ChatServer(numberPort)).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        chatServer.close();
        findAgentSystem.dropDatabase();
    }

    private void config() {
        final String PATH = "tar.txt";
        serverAnswer = new EnumMap(AnswerCode.class);
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(PATH))))) {
            String line = fileReader.readLine();
            if (line != null && !"".equals(line)) {
                numberPort = Integer.valueOf(line);
                line = fileReader.readLine();
            }
            while (line != null && !"".equals(line)) {
                serverAnswer.put(AnswerCode.getEnumByInt(Integer.valueOf(line.substring(0, line.indexOf('|')))), line.substring(line.indexOf('|') + 1, line.length()));
                line = fileReader.readLine();
            }
        } catch (FileNotFoundException e) {
            log.error("Config file isn't exist", e);
        } catch (IOException e) {
            log.error("Problem with input stream with config file", e);
        }
    }
}
