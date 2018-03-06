package com.touchsoft.test;

import com.touchsoft.Client;
import com.touchsoft.SocketHandler;
import com.touchsoft.FindAgentSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;

public class InMemoryDatabaseTest {
    private Client client;
    private Client agent;
    private SocketHandler socketHandlerAgent;
    private SocketHandler socketHandlerClient;

    @Before
    public void init(){
        FindAgentSystem.createDatabase();
        socketHandlerAgent=mock(SocketHandler.class);
        socketHandlerClient=mock(SocketHandler.class);
    }

    @After
    public void finaly(){
        socketHandlerAgent=null;
        socketHandlerClient=null;
        client=null;
        agent=null;
        FindAgentSystem.clear();
        FindAgentSystem.dropDatabase();
    }

    @Test
    public void addClientOnDatabase(){
        client=new Client("stas",socketHandlerClient,false);
        FindAgentSystem.addUser(client);
        FindAgentSystem.removeUser(client);
        Assert.assertTrue(FindAgentSystem.findUser("stas"));
    }

    @Test
    public void addAgentOnDatabase(){
        agent=new Client("stas",socketHandlerClient,true);
        FindAgentSystem.addAgent(agent);
        FindAgentSystem.removeAgent(agent);
        Assert.assertTrue(FindAgentSystem.findAgent("stas"));
    }

    @Test
    public void addClientOnDatabaseFindAgent(){
        client=new Client("stas",socketHandlerClient,false);
        FindAgentSystem.addUser(client);
        FindAgentSystem.removeUser(client);
        Assert.assertFalse(FindAgentSystem.findAgent("stas"));
    }

    @Test
    public void addAgentOnDatabaseFindClient(){
        agent=new Client("stas",socketHandlerClient,true);
        FindAgentSystem.addAgent(agent);
        FindAgentSystem.removeAgent(agent);
        Assert.assertFalse(FindAgentSystem.findUser("stas"));
    }

    @Test
    public void loginDatabaseAgent(){
        agent=new Client("stas",socketHandlerClient,true);
        FindAgentSystem.addAgent(agent);
        FindAgentSystem.removeAgent(agent);
        Assert.assertTrue(FindAgentSystem.login("stas","Agent"));
    }

    @Test
    public void loginDatabaseClient(){
        client =new Client("stas",socketHandlerClient,true);
        FindAgentSystem.addUser(client);
        FindAgentSystem.removeUser(client);
        Assert.assertTrue(FindAgentSystem.login("stas","Client"));
    }

}


