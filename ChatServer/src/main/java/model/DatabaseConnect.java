package model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

public class DatabaseConnect {
    private static DatabaseConnect databaseConnect = null;
    private static Logger log = LoggerFactory.getLogger(DatabaseConnect.class);
    private final String url = "jdbc:derby:memory:ServerChatDB";
    private Connection connection;
    private PreparedStatement stmt;

    public DatabaseConnect() {
        createDatabase();
    }

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

    public void addInDatabase(User client) {
        String tableName = client.getRole() == Role.CLIENT ? "Client" : "Agent";
        try {
            stmt = connection.prepareStatement("INSERT INTO " + tableName + "(name) VALUES (?)");
            stmt.setString(1, client.getLogin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.warn("Error addInDatabase ", e);
        }
    }

    public boolean findInDatabase(User client) {
        String tableName = client.getRole() == Role.CLIENT ? "Client" : "Agent";
        try {
            stmt = connection.prepareStatement("SELECT name FROM " + tableName + " WHERE name=?");
            stmt.setString(1, client.getLogin());
            ResultSet rst = stmt.executeQuery();
            if (rst.next()) return true;
            else return false;
        } catch (SQLException e) {
            log.warn("Error findInDatabase", e);
            return true;
        }
    }

    public void removeFromDatabase(User client) {
        String tableName = client.getRole() == Role.CLIENT ? "Client" : "Agent";
        try {
            stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE name=?");
            stmt.setString(1, client.getLogin());
            stmt.executeUpdate();
        } catch (SQLException e) {
            log.warn("Error login", e);
        }
    }


}
