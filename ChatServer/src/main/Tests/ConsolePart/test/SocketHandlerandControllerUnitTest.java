package ConsolePart.test;

import com.google.gson.Gson;
import model.*;
import ConsolePart.SocketHandler;
import org.junit.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SocketHandlerandControllerUnitTest {
    private static DataManipulate dataManipulate;
    private SocketHandler testSocketHandler;
    private ByteArrayOutputStream outputStream;
    private ByteArrayInputStream inputStream;
    private Gson gson = new Gson();
    private Socket socket;

    @BeforeClass
    public static void construct() {
        dataManipulate = DataManipulate.getInstance();
    }

    @Before
    public void init() throws IOException {
        outputStream = new ByteArrayOutputStream();
        socket = mock(Socket.class);
        when(socket.getOutputStream()).thenReturn(outputStream);
        when(socket.isClosed()).thenReturn(false);
    }

    @After
    public void clean() throws IOException {
        outputStream.close();
        inputStream.close();
        FindAgentSystem.getInstance().clear();
        dataManipulate.clear();
    }

    @Test
    public void registerAgentTest() throws IOException {
        String line = gson.toJson(new CommandContainer(AnswerCode.REGISTER, "Stas", Role.AGENT));
        inputStream = new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(new CommandContainer("Stas", Role.AGENT, AnswerCode.GOOD_REGISTER)) + '\n', outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameAgentTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User1", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User1", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User1", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerClientTest() throws IOException {
        String line = gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User2", Role.CLIENT));
        inputStream = new ByteArrayInputStream(line.getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        Assert.assertEquals(gson.toJson(new CommandContainer("User2", Role.CLIENT, AnswerCode.GOOD_REGISTER)) + "\n", outputStream.toString("UTF-8"));
    }

    @Test
    public void registerSameNameClientTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User3", Role.CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User3", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User3", Role.CLIENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void leaveTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LEAVE_CHAT, null, null)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.NEED_REGISTER_OR_LOGIN)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void exitTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT, null, null)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.EXIT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void loginTest() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User5", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_CLIENT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }


    @Test
    public void registerAndExitAgent() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User6", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT, "User6", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User6", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.EXIT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndExitClient() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User7", Role.CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT, "User7", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User7", Role.CLIENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.EXIT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void messageWithoutRegistration() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("User8", null, "hello")));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.NEED_REGISTER_OR_LOGIN)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerMessageClient() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User9", Role.CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User9", Role.CLIENT, "Hello World")));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer("User9", Role.CLIENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.NO_AGENT_WAIT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerMessageAgent() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User10", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User10", Role.AGENT, "Hello World")));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User10", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightTypeOfUser() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User11", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User11", Role.CLIENT, "Hello World")));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User11", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void notRightTypeOfUserInRegistration() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User12", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User12", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        Assert.assertNotEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndLeaveAgent() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User12", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LEAVE_CHAT, "User12", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User12", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.CAN_NOT_LEAVE_AGENT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void registerAndLeaveClient() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User13", Role.CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LEAVE_CHAT, "User13", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User13", Role.CLIENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CHAT)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginAfterRegisterName() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User14", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.EXIT, "User14", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User14", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User14", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.EXIT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginSameNameClient() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User15", Role.CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User15", Role.CLIENT, "Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User15", Role.CLIENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User15", Role.CLIENT)));
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1 = new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1 = new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer("User15", Role.CLIENT, AnswerCode.GOOD_LOGIN)));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User15", Role.CLIENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.NO_AGENT_WAIT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(), outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void LoginSameNameAgent() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User16", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User16", Role.AGENT, "Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User16", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User16", Role.AGENT)));
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1 = new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1 = new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer("User16", Role.AGENT, AnswerCode.GOOD_LOGIN)));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User16", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(), outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

    @Test
    public void RegisterAgentAfterLoginClientSameName() throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.REGISTER, "User17", Role.AGENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("User17", Role.AGENT, "Hello")));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User17", Role.AGENT)));
        inputStream = new ByteArrayInputStream(stringBuilder.toString().getBytes("UTF-8"));
        when(socket.getInputStream()).thenReturn(inputStream);
        testSocketHandler = new SocketHandler(socket);
        testSocketHandler.run();
        //************************************************
        StringBuilder stringBuilder1 = new StringBuilder();
        stringBuilder1.append(gson.toJson(new CommandContainer(AnswerCode.LOGIN, "User17", Role.CLIENT)));
        ByteArrayOutputStream outputStream1 = new ByteArrayOutputStream();
        Socket socket1 = mock(Socket.class);
        when(socket1.getOutputStream()).thenReturn(outputStream1);
        when(socket1.isClosed()).thenReturn(false);
        ByteArrayInputStream inputStream1 = new ByteArrayInputStream(stringBuilder1.toString().getBytes("UTF-8"));
        when(socket1.getInputStream()).thenReturn(inputStream1);
        SocketHandler testSocketHandler1 = new SocketHandler(socket1);
        testSocketHandler1.run();
        //****************************************************
        stringBuilder1 = new StringBuilder("");
        stringBuilder1.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_CLIENT)));
        stringBuilder1.append("\n");
        //***************************************************
        stringBuilder = new StringBuilder("");
        stringBuilder.append(gson.toJson(new CommandContainer("User17", Role.AGENT, AnswerCode.GOOD_REGISTER)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.DONT_HAVE_CLIENT)));
        stringBuilder.append("\n");
        stringBuilder.append(gson.toJson(new CommandContainer("Server", null, AnswerCode.YOU_REGISTER_OR_LOGIN_YET)));
        stringBuilder.append("\n");
        Assert.assertEquals(stringBuilder1.toString(), outputStream1.toString("UTF-8"));
        Assert.assertEquals(stringBuilder.toString(), outputStream.toString("UTF-8"));
    }

}