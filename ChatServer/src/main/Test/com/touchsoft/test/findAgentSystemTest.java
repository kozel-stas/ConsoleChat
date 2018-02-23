package com.touchsoft.test;

import com.google.gson.Gson;
import com.touchsoft.CommandContainer;
import com.touchsoft.SocketHandler;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class findAgentSystemTest {
    private SocketHandler AgentSocketHandler;
    private SocketHandler ClientSocketHandler;
    private ByteArrayOutputStream AgentOutputStream;
    private ByteArrayInputStream AgentInputStream;
    private ByteArrayOutputStream ClientOutputStream;
    private ByteArrayInputStream ClientInputStream;
    private Gson gson=new Gson();
    private Socket socketAgent;
    private Socket socketClient;


    @Before
    public void init() throws IOException {
        ClientOutputStream=new ByteArrayOutputStream();
        AgentOutputStream=new ByteArrayOutputStream();
        socketClient=mock(Socket.class);
        socketAgent=mock(Socket.class);
        when(socketAgent.getOutputStream()).thenReturn(AgentOutputStream);
        when(socketAgent.isClosed()).thenReturn(false);
        when(socketClient.getOutputStream()).thenReturn(ClientOutputStream);
        when(socketClient.isClosed()).thenReturn(false);
    }
    @After
    public void clean() throws IOException{
        AgentOutputStream.close();
        AgentInputStream.close();
        ClientOutputStream.close();
        ClientInputStream.close();
    }

    @Test
    public void testFindAgent () throws IOException, InterruptedException {
        StringBuilder stringBuilderAgent=new StringBuilder();
        stringBuilderAgent.append(gson.toJson(new CommandContainer("/register agent stas")));
        AgentInputStream =new ByteArrayInputStream(stringBuilderAgent.toString().getBytes("UTF-8"));
        when(socketAgent.getInputStream()).thenReturn(AgentInputStream);
        AgentSocketHandler=new SocketHandler(socketAgent);
        Thread thread=new Thread(AgentSocketHandler);
        thread.start();
        //*************************************************
        StringBuilder stringBuilderClient=new StringBuilder();
        stringBuilderClient.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilderClient.append("\n");
        stringBuilderClient.append(gson.toJson(new CommandContainer("stas",false,"Hello")));
        ClientInputStream =new ByteArrayInputStream(stringBuilderClient.toString().getBytes("UTF-8"));
        when(socketClient.getInputStream()).thenReturn(ClientInputStream);
        ClientSocketHandler=new SocketHandler(socketClient);
        Thread.currentThread().sleep(200);
        ClientSocketHandler.run();
        //**************************************************
        stringBuilderAgent=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilderAgent.append("\n");

        stringBuilderClient=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));


        Assert.assertEquals(stringBuilderAgent.toString(),AgentOutputStream.toString("UTF-8"));
        Assert.assertEquals(stringBuilderClient.toString(),ClientOutputStream.toString("UTF-8"));

    }
}
