package com.touchsoft.test;

import com.google.gson.Gson;
import com.touchsoft.CommandContainer;
import com.touchsoft.SocketHandler;
import com.touchsoft.findAgentSystem;
import org.junit.*;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SocketHandlerandControllerUnitTest {
    private SocketHandler testSocketHandler;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Gson gson=new Gson();
    private Socket socket;
    private static ArrayList<String> serverAnswer=new ArrayList<>();

    @BeforeClass
    public static void setServerAnswer(){
        serverAnswer.add(0,"Вы должны авторизироваться или зарегистрироваться");
        serverAnswer.add(1,"Непредвиденная ошибка");
        serverAnswer.add(2,"Неверная команда");
        serverAnswer.add(3,"У вас нет активной беседы");
        serverAnswer.add(4,"Вы покинули беседу");
        serverAnswer.add(5,"Нельзя отключаться агентам с клиентом в сети");
        serverAnswer.add(6,"К сожалению, свободных агентов нет, мы уведовим вас когда вас подключат");
        serverAnswer.add(7,"Первый освободившийся агент ответит вам");
        serverAnswer.add(8,"У вас нет подключенных клиентов");
        serverAnswer.add(9,"Неверно введен тип пользователя");
        serverAnswer.add(10,"Недопустимые символы в имени");
        serverAnswer.add(11,"Клиент с таким именем уже в сети");
        serverAnswer.add(12,"Нет такого зарегистрированного клиента");
        serverAnswer.add(13,"Агент с таким именем уже в сети");
        serverAnswer.add(14,"Нет такого зарегистрированного агента");
        serverAnswer.add(15,"Выбранное имя уже занято");
        serverAnswer.add(16,"Клиент отключился");
        serverAnswer.add(17,"Агент отключился");
        serverAnswer.add(18,"Агент отключился, первый освободившийся агент ответит вам");
        serverAnswer.add(19,"К вам подключился агент ");
        serverAnswer.add(20,"Вы подключены к клиенту ");
        serverAnswer.add(21,"Вы уже зарегистрировались или авторизовались");
    }

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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(serverAnswer)+"\n"+gson.toJson(new CommandContainer("stas",true,"goodRegister"))+"\n",outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameAgentTest () throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void registerClientTest () throws IOException {
        String line=gson.toJson(new CommandContainer("/register client stas"));
        inputStream =new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(serverAnswer)+"\n"+gson.toJson(new CommandContainer("stas",false,"goodRegister"))+"\n",outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameClientTest () throws IOException {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("/register client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void leaveTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/leave")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(0,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void unknownCommandTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/ext")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(2,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void exitTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/exit")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(666,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void loginTest() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/login client stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(12, "Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(9,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(2,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(9,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(10,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(666,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(666,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void messageWithoutRegistration() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"hello")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(0,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(6,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(8,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(8,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightTypeOfUserInRegistration() throws IOException{
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("/register agent stas")));
        inputStream =new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(3,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(3,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
        testSocketHandler.run();
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(666,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
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
        SocketHandler testSocketHandler1=new SocketHandler(socket1,serverAnswer);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder1.append("\n");
        stringBuilder1.append(gson.toJson(new CommandContainer("stas",false,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(6,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
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
        SocketHandler testSocketHandler1=new SocketHandler(socket1,serverAnswer);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder1.append("\n");
        stringBuilder1.append(gson.toJson(new CommandContainer("stas",true,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(8,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
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
        testSocketHandler=new SocketHandler(socket,serverAnswer);
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
        SocketHandler testSocketHandler1=new SocketHandler(socket1,serverAnswer);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder1.append("\n");
        stringBuilder1.append(gson.toJson(new CommandContainer(12,"Server")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder(gson.toJson(serverAnswer));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(8,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(21,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(),outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }
}
