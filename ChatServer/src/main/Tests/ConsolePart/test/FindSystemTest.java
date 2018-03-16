package ConsolePart.test;

import ConsolePart.SocketHandler;
import model.AnswerCode;
import model.Client;
import model.CommandContainer;
import model.FindAgentSystem;
import org.junit.*;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


public class FindSystemTest {
    private static FindAgentSystem findAgentSystem;
    private Client client;
    private Client agent;
    private SocketHandler socketHandlerAgent;
    private SocketHandler socketHandlerClient;
    private CommandContainer answerAgent;
    private CommandContainer answerClient;

    @Before
    public void init() {
        findAgentSystem=FindAgentSystem.getInstance();
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
        findAgentSystem.clear();
    }

    @Test
    public void findSystemTestAgentClient() {
        client = new Client("stas", socketHandlerClient, false);
        agent = new Client("stas", socketHandlerAgent, true);
        Assert.assertFalse(findAgentSystem.findSystem(agent));
        Assert.assertTrue(findAgentSystem.findSystem(client));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "stas").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClient() {
        client = new Client("stas", socketHandlerClient, false);
        Assert.assertFalse(findAgentSystem.findSystem(client));
    }

    @Test
    public void findSystemTestAgent() {
        agent = new Client("stas", socketHandlerClient, true);
        Assert.assertFalse(findAgentSystem.findSystem(agent));
    }

    @Test
    public void findSystemTestClientAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestSomeClientAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Client testClient = new Client("WebPart", mock(SocketHandler.class), false);
        Client testClient1 = new Client("Test1", mock(SocketHandler.class), false);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertFalse(findAgentSystem.findSystem(testClient));
        Assert.assertFalse(findAgentSystem.findSystem(testClient1));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClientSomeAgent() {
        agent = new Client("Vlad", socketHandlerAgent, true);
        client = new Client("Stas", socketHandlerClient, false);
        Client testAgent = new Client("WebPart", mock(SocketHandler.class), true);
        Client testAgent1 = new Client("Test1", mock(SocketHandler.class), true);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertFalse(findAgentSystem.findSystem(testAgent));
        Assert.assertFalse(findAgentSystem.findSystem(testAgent1));
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_CLIENT, "Stas").toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer(AnswerCode.NEW_AGENT, "Vlad").toString(), answerClient.toString());
    }

}
