package com.touchsoft.test;

import com.google.gson.Gson;
import com.touchsoft.CommandContainer;
import com.touchsoft.SocketHandler;
import com.touchsoft.findAgentSystem;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.*;
import java.net.Socket;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SocketHandlerandControllerUnitTest {
    private SocketHandler testSocketHandler;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Gson gson=new Gson();
    private Socket socket;


    @Before
    public void init() throws IOException{
        findAgentSystem.createDatabase();
        outputStream=new ByteArrayOutputStream();
        socket=mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isClosed()).thenReturn(false);
    }
    @After
    public void clean() throws IOException{
        outputStream.close();
        inputStream.close();
        findAgentSystem.dropDatabase();
    }

    @Test
    public void registerAgentTest () throws IOException {
        String line=gson.toJson(new CommandContainer("/register agent stas"));
        inputStream =new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(new CommandContainer("stas",true,"good"))+"\n",outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameAgentTest () throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerClientTest () throws IOException {
        String line=gson.toJson(new CommandContainer("/register client stas"));
        inputStream =new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(new CommandContainer("stas",false,"good"))+"\n",outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameClientTest () throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void leaveTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/leave")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("Вы должны авторизироваться или зарегистрироваться","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void unknownCommandTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/ext")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("Неверная команда","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void exitTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/exit")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("exit","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void loginTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/login client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("Нет такого зарегистрированного клиента","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightClientTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register stas ")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register clien stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas ")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("Неверно введен тип пользователя","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Неверная команда","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Неверно введен тип пользователя","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Недопустимые символы в имени","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndExitAgent() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/exit")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("exit","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndExitClient() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/exit")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("exit","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void messageWithoutRegistration() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"hello")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("Вы должны авторизироваться или зарегистрироваться","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerMessageClient() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"Hello World")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerMessageAgent() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"Hello World")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет подключенных клиентов","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightTypeOfUser() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"Hello World")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет подключенных клиентов","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightTypeOfUserInRegistration() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        Assert.assertNotEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndLeaveAgent() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/leave")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет активной беседы","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndLeaveClient() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/leave")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет активной беседы","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginAfterRegisterName() throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/exit")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/login client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("exit","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginSameNameClient() throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/login client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1=new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer("/login client stas")));
        ByteArrayOutputStream outputStream1=new ByteArrayOutputStream();
        Socket socket1=mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 =new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1=new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",false,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(),outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginSameNameAgent() throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/login agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1=new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer("/login agent stas")));
        ByteArrayOutputStream outputStream1=new ByteArrayOutputStream();
        Socket socket1=mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 =new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1=new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет подключенных клиентов","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(),outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void RegisterAgentAfterLoginClientSameName() throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/login agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1=new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer("/login client stas")));
        ByteArrayOutputStream outputStream1=new ByteArrayOutputStream();
        Socket socket1=mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 =new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1=new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(new CommandContainer("Нет такого зарегистрированного клиента","Server")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(new CommandContainer("stas",true,"good")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("У вас нет подключенных клиентов","server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Вы уже зарегистрировались или авторизовались","server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(),outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }
}
