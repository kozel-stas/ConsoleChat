package model;

import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.SupportClasses.Role;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataManipulate {
    private static DataManipulate dataManipulate = null;
    private static DatabaseConnect databaseConnect;
    private static FindAgentSystem findAgentSystem;
    private static Logger log = LoggerFactory.getLogger(DataManipulate.class);
    private ConcurrentMap<String, User> clients = new ConcurrentHashMap();
    private ConcurrentMap<String, User> agents = new ConcurrentHashMap();

    private DataManipulate() {
        databaseConnect = new DatabaseConnect();
        findAgentSystem = FindAgentSystem.getInstance();
    }

    public synchronized static DataManipulate getInstance() {
        if (dataManipulate == null) dataManipulate = new DataManipulate();
        return dataManipulate;
    }

    public User getUser(String login, Role role) {
        if (Role.CLIENT == role) {
            return clients.get(login);
        } else {
            return agents.get(login);
        }
    }

    public boolean find(User user) {
        if (user.getRole() == Role.CLIENT) return findClient(user);
        else return findAgent(user);
    }

    public boolean find(String login,Role role){
        return databaseConnect.findInDatabase(new User(login,null,role,null));
    }

    private boolean findClient(User client) {
        if (clients.get(client.getLogin()) != null) return true;
        return false;
    }

    private boolean findAgent(User agent) {
        if (agents.get(agent.getLogin()) != null)
            return true;
        return false;
    }

    public void add(User user) {
        if (user.getRole() == Role.CLIENT) addClient(user);
        else addAgent(user);
    }

    private void addClient(User client) {
        clients.put(client.getLogin(), client);
    }

    private void addAgent(User agent) {
        agents.put(agent.getLogin(), agent);
    }

    public boolean remove(User user) {
        if (user.getRole() == Role.CLIENT) return removeClient(user);
        else return removeAgent(user);
    }

    private boolean removeAgent(User agent) {
        if (agents.remove(agent.getLogin(), agent)) {
            databaseConnect.addInDatabase(agent);
            return true;
        }
        return false;
    }

    private boolean removeClient(User client) {
        if (clients.remove(client.getLogin(), client)) {
            databaseConnect.addInDatabase(client);
            return true;
        }
        return false;
    }

    public CommandContainer register(User user) {
        if (find(user) || databaseConnect.findInDatabase(user))
            return new CommandContainer("Server", null, AnswerCode.NAME_ALREADY_USED);
        add(user);
        CommandContainer commandContainer = new CommandContainer(user.getLogin(), user.getRole(), AnswerCode.GOOD_REGISTER);
        if (user.getSocket() != null) user.getSocket().send(commandContainer);
        if (user.getSocket() !=null && user.getRole() == Role.AGENT) {
            findAgentSystem.findSystem(user);
            log.info("register new agent", user);
        } else log.info("register new client", user);
        return commandContainer;
    }

    public CommandContainer login(User user) {
        if (find(user))
            if (user.getRole() == Role.CLIENT)
                return new CommandContainer("Server", null, AnswerCode.CLIENT_ONLINE_YET);
            else return new CommandContainer("Server", null, AnswerCode.AGENT_ONLINE_YET);
        if (!databaseConnect.findInDatabase(user))
            if (user.getRole() == Role.CLIENT)
                return new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_CLIENT);
            else return new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_AGENT);
        databaseConnect.removeFromDatabase(user);
        add(user);
        CommandContainer commandContainer = new CommandContainer(user.getLogin(), user.getRole(), AnswerCode.GOOD_LOGIN);
        if (user.getSocket() != null) user.getSocket().send(commandContainer);
        if (user.getSocket() !=null && user.getRole() == Role.AGENT) {
            findAgentSystem.findSystem(user);
            log.info("Login agent", user);
        } else log.info("Login client", user);
        return new CommandContainer(user.getLogin(), user.getRole(), AnswerCode.GOOD_LOGIN);
    }

    public Collection<User> getRegisterAgent(){
        Collection<User> users =databaseConnect.getAllUser(Role.AGENT);
       return CollectionUtils.union(users,agents.values());
    }

    public Collection<User> getRegisterClient(){
        Collection<User> users =databaseConnect.getAllUser(Role.CLIENT);
        return CollectionUtils.union(users,clients.values());
    }

    public void clear() {
        agents.clear();
        clients.clear();
    }
}
