package ConsolePart;

import com.google.gson.Gson;
import model.ChatInterface;
import model.CommandContainer;
import model.FindAgentSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.IOException;
import java.net.Socket;


public class SocketHandler implements Runnable, ChatInterface {
    private Logger log = LoggerFactory.getLogger(SocketHandler.class);
    private Socket connect;
    private BufferedReader input;
    private BufferedWriter output;
    private Controller controller;
    private FindAgentSystem findAgentSystem;
    private Gson json;

    public SocketHandler(Socket connect) {
        this.findAgentSystem = FindAgentSystem.getInstance();
        this.connect = connect;
        try {
            input = new BufferedReader(new InputStreamReader(connect.getInputStream(), "UTF-8"));
            output = new BufferedWriter(new OutputStreamWriter(connect.getOutputStream(), "UTF-8"));
        } catch (IOException e) {
            log.error("IOException in start SocketHandler", e);
        }
        controller = new Controller(this);
        json = new Gson();
    }

    public void run() {
        CommandContainer command;
        while (!connect.isClosed()) {
            try {
                command = json.fromJson(input.readLine(), CommandContainer.class);
                if (command == null) {
                    close();
                    break;
                } else controller.handler(command);
            } catch (IOException ex) {
                close();
                log.warn("Error reading command", ex);
            }
        }
    }

    public void waitAgent() {
        controller.waitAgent();
    }

    public void notWaitAgent() {
        controller.notWaitAgent();
    }

    public void updateBufferedMessage() {
        controller.updateBufferedMessage();
    }

    public void send(CommandContainer container) {
        send(json.toJson(container));
    }

    @Override
    public void close() {
        if (!connect.isClosed()) {
            try {
                controller.close();
                connect.close();
            } catch (IOException ex) {
                log.error("Error closing connection", ex);
            }
        }
    }

    public void send(String msg) {
        if (!connect.isClosed()) {
            synchronized (this) {
                try {
                    output.write(msg);
                    output.write("\n");
                    output.flush();
                } catch (IOException ex) {
                    close();
                    log.error("Error sending message", ex);
                }
            }
        }
    }

    public FindAgentSystem getFindAgentSystem() {
        return findAgentSystem;
    }

}