package com.touchsoft.test;

import com.google.gson.Gson;
import com.touchsoft.AnswerCode;
import com.touchsoft.CommandContainer;
import com.touchsoft.SocketHandler;
import com.touchsoft.FindAgentSystem;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;

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
        FindAgentSystem.createDatabase();
        outputStream=new ByteArrayOutputStream();
        socket=mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isClosed()).thenReturn(false);
    }
    @After
    public void clean() throws IOException{
        outputStream.close();
        inputStream.close();
        FindAgentSystem.dropDatabase();
    }

    @Test
    public void registerAgentTest () throws IOException {
        String line=gson.toJson(new CommandContainer("/register agent stas"));
        inputStream =new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(new CommandContainer("stas",true,"goodRegister"))+"\n",outputStream.toString("UTF-8"));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
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
        Assert.assertEquals(gson.toJson(new CommandContainer("stas",false,"goodRegister"))+"\n",outputStream.toString("UTF-8"));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.UNKNOWN_COMMAND,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_CLIENT, "Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.UNKNOWN_COMMAND,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.UNKNOWN_TYPE_USER,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.INVALID_CHARACTERS,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.NEED_REGISTER_OR_LOGIN,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.NO_AGENT_WAIT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT,"Server")));
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
        stringBuilder=new StringBuilder("");
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
        testSocketHandler=new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CHAT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CHAT,"Server")));
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
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
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
        stringBuilder1=new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer("stas",false,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",false,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.NO_AGENT_WAIT,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
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
        stringBuilder1=new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer("stas",true,"goodLogin")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
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
        stringBuilder1=new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_REGISTER_CLIENT,"Server")));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder=new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("stas",true,"goodRegister")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.DONT_HAVE_CLIENT,"Server")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.YOU_REGISTER_OR_LOGIN_YET,"Server")));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(),outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(),outputStream.toString("UTF-8"));
    }
}
