package com.touchsoft.test;

import com.touchsoft.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


public class findSystemTest {
    private Client client;
    private Client agent;
    private SocketHandler socketHandlerAgent;
    private SocketHandler socketHandlerClient;
    private CommandContainer answerAgent;
    private CommandContainer answerClient;

    @Before
    public void init() {
        socketHandlerAgent = mock(SocketHandler.class);
        socketHandlerClient = mock(SocketHandler.class);
        doAnswer((Answer) invocation -> {
            Object arg0 = invocation.getArguments()[0];
            answerAgent = (CommandContainer) arg0;
            return answerAgent;
        }).when(socketHandlerAgent).send(any(CommandContainer.class));
        doAnswer((Answer) invocation -> {
            Object arg0 = invocation.getArguments()[0];
            answerClient = (CommandContainer) arg0;
            return answerClient;
        }).when(socketHandlerClient).send(any(CommandContainer.class));
    }

    @After
    public void finaly() {
        socketHandlerAgent = null;
        socketHandlerClient = null;
        client = null;
        agent = null;
        answerAgent = null;
        answerClient = null;
        FindAgentSystem.clear();
    }

    @Test
    public void findSystemTestAgentClient() {
        client = new Client("stas", socketHandlerClient, false);
        agent = new Client("stas", socketHandlerAgent, true);
        Assert.assertFalse(FindAgentSystem.findSystem(agent));
        Assert.assertTrue(FindAgentSystem.findSystem(client));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "stas").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClient() {
        client = new Client("stas", socketHandlerClient, false);
        Assert.assertFalse(FindAgentSystem.findSystem(client));
    }

    @Test
    public void findSystemTestAgent() {
        agent = new Client("stas", socketHandlerClient, true);
        Assert.assertFalse(FindAgentSystem.findSystem(agent));
    }

    @Test
    public void findSystemTestClientAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Assert.assertFalse(FindAgentSystem.findSystem(client));
        Assert.assertTrue(FindAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestSomeClientAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Client testClient = new Client("Test", mock(SocketHandler.class), false);
        Client testClient1 = new Client("Test1", mock(SocketHandler.class), false);
        Assert.assertFalse(FindAgentSystem.findSystem(client));
        Assert.assertFalse(FindAgentSystem.findSystem(testClient));
        Assert.assertFalse(FindAgentSystem.findSystem(testClient1));
        Assert.assertTrue(FindAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClientSomeAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Client testAgent = new Client("Test", mock(SocketHandler.class), true);
        Client testAgent1 = new Client("Test1", mock(SocketHandler.class), true);
        Assert.assertFalse(FindAgentSystem.findSystem(client));
        Assert.assertTrue(FindAgentSystem.findSystem(agent));
        Assert.assertFalse(FindAgentSystem.findSystem(testAgent));
        Assert.assertFalse(FindAgentSystem.findSystem(testAgent1));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

}
