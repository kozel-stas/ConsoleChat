package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

public class FindAgentSystem {
    private static Logger log = LoggerFactory.getLogger(FindAgentSystem.class);
    private final String url = "jdbc:derby:memory:ServerChatDB";
    private Connection connection;
    private PreparedStatement stmt;
    private ConcurrentLinkedQueue<Client> waitAgents = new ConcurrentLinkedQueue();
    private ConcurrentLinkedQueue<Client> waitUsers = new ConcurrentLinkedQueue();
    private CopyOnWriteArrayList<Client> users = new CopyOnWriteArrayList();
    private CopyOnWriteArrayList<Client> agents = new CopyOnWriteArrayList();

    public void createDatabase() {
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
            connection = DriverManager.getConnection(url + ";create=true");
            String createAgentTable = "CREATE TABLE Agent (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1), name VARCHAR(100) NOT NULL)";
            stmt = connection.prepareStatement(createAgentTable);
            stmt.executeUpdate();
            String createClientTable = "CREATE TABLE Client (id INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1,INCREMENT BY 1), name VARCHAR(100) NOT NULL)";
            stmt = connection.prepareStatement(createClientTable);
            stmt.executeUpdate();
        } catch (ClassNotFoundException e) {
            log.error("Error when database is starting", e);
        } catch (SQLException e) {
            log.error("Error when database is starting", e);
        }
    }

    public void dropDatabase() {
        try {
            connection = DriverManager.getConnection(url + ";drop=true");
        } catch (SQLException e) {
            log.warn("Drop database", e);
        }
    }

    public boolean login(String name, String type) {
        if (findInDatabase(name, type)) {
            try {
                stmt = connection.prepareStatement("DELETE FROM " + type + " WHERE name=?");
                stmt.setString(1, name);
                stmt.executeUpdate();
            } catch (SQLException e) {
                log.warn("Error login", e);
            }
            return true;
        } else {
            return false;
        }
    }

    private void addInDatabase(Client client, String type) {
        try {
            stmt = connection.prepareStatement("INSERT INTO " + type + "(name) VALUES (?)");
            stmt.setString(1, client.getName());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.warn("Error addInDatabase ", e);
        }
    }

    private boolean findInDatabase(String name, String type) {
        try {
            stmt = connection.prepareStatement("SELECT name FROM " + type + " WHERE name=?");
            stmt.setString(1, name);
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) return true;
            else return false;
        } catch (SQLException e) {
            log.warn("Error findInDatabase", e);
            return true;
        }

    }

    public boolean findSystem(Client client) {
        if (client.isAgent()) {
            if (waitUsers.size() > 0) {
                synchronized (FindAgentSystem.class) {
                    Client user = waitUsers.poll();
                    user.setRecipient(client);
                    client.setRecipient(user);
                    user.getMysocket().notWaitAgent();
                    user.getMysocket().send(new CommandContainer(AnswerCode.NEW_AGENT, client.getName()));
                    client.getMysocket().send(new CommandContainer(AnswerCode.NEW_CLIENT, user.getName()));
                }
                return true;
            } else {
                waitAgents.add(client);
                return false;
            }
        } else {
            if (waitAgents.size() > 0) {
                synchronized (FindAgentSystem.class) {
                    Client agent = waitAgents.poll();
                    agent.setRecipient(client);
                    client.setRecipient(agent);
                    client.getMysocket().notWaitAgent();
                    client.getMysocket().send(new CommandContainer(AnswerCode.NEW_AGENT, agent.getName()));
                    agent.getMysocket().send(new CommandContainer(AnswerCode.NEW_CLIENT, client.getName()));
                }
                return true;
            } else {
                client.getMysocket().waitAgent();
                waitUsers.add(client);
                return false;
            }
        }
    }

    public boolean findUser(String name) {
        Iterator iterator = users.iterator();
        while (iterator.hasNext()) {
            if (((Client) iterator.next()).getName().equals(name)) return true;
        }
        return findInDatabase(name, "Client");
    }

    public boolean findAgent(String name) {
        Iterator iterator = agents.iterator();
        while (iterator.hasNext()) {
            if (((Client) iterator.next()).getName().equals(name)) return true;
        }
        return findInDatabase(name, "Agent");
    }

    public void addUser(Client user) {
        users.add(user);
    }

    public void addAgent(Client agent) {
        agents.add(agent);
    }

    public void removeAgent(Client agent) {
        waitAgents.remove(agent);
        agents.remove(agent);
        addInDatabase(agent, "Agent");
    }

    public void removeUser(Client user) {
        waitUsers.remove(user);
        users.remove(user);
        addInDatabase(user, "Client");
    }

    public void clear() {
        waitAgents.clear();
        waitUsers.clear();
        agents.clear();
        users.clear();
    }

}