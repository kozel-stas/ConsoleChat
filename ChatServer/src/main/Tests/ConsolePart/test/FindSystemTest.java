package ConsolePart.test;

import ConsolePart.SocketHandler;
import model.*;
import model.SupportClasses.AnswerCode;
import model.SupportClasses.CommandContainer;
import model.SupportClasses.Role;
import model.User;
import org.junit.*;
import org.mockito.stubbing.Answer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


public class FindSystemTest {
    private static FindAgentSystem findAgentSystem;
    private User client;
    private User agent;
    private SocketHandler socketHandlerAgent;
    private SocketHandler socketHandlerClient;
    private CommandContainer answerAgent;
    private CommandContainer answerClient;

    @Before
    public void init() {
        findAgentSystem = FindAgentSystem.getInstance();
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
        client = new User("stas", socketHandlerClient, Role.CLIENT, null);
        agent = new User("stas", socketHandlerAgent, Role.AGENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(agent));
        Assert.assertTrue(findAgentSystem.findSystem(client));
        Assert.assertEquals(new CommandContainer("stas", null, AnswerCode.NEW_CLIENT).toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer("stas", null, AnswerCode.NEW_AGENT).toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClient() {
        client = new User("stas", socketHandlerClient, Role.CLIENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(client));
    }

    @Test
    public void findSystemTestAgent() {
        agent = new User("stas", socketHandlerClient, Role.AGENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(agent));
    }

    @Test
    public void findSystemTestClientAgent() {
        agent = new User("Vlad", socketHandlerAgent, Role.AGENT, null);
        client = new User("Stas", socketHandlerClient, Role.CLIENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer("Stas", null, AnswerCode.NEW_CLIENT).toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer("Vlad", null, AnswerCode.NEW_AGENT).toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestSomeClientAgent() {
        agent = new User("Vlad", socketHandlerAgent, Role.AGENT, null);
        client = new User("Stas", socketHandlerClient, Role.CLIENT, null);
        User testClient = new User("WebPart", mock(SocketHandler.class), Role.CLIENT, null);
        User testClient1 = new User("Test1", mock(SocketHandler.class), Role.CLIENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertFalse(findAgentSystem.findSystem(testClient));
        Assert.assertFalse(findAgentSystem.findSystem(testClient1));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertEquals(new CommandContainer("Stas", null, AnswerCode.NEW_CLIENT).toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer("Vlad", null, AnswerCode.NEW_AGENT).toString(), answerClient.toString());
    }

    @Test
    public void findSystemTestClientSomeAgent() {
        agent = new User("Vlad", socketHandlerAgent, Role.AGENT, null);
        client = new User("Stas", socketHandlerClient, Role.CLIENT, null);
        User testAgent = new User("WebPart", mock(SocketHandler.class), Role.AGENT, null);
        User testAgent1 = new User("Test1", mock(SocketHandler.class), Role.AGENT, null);
        Assert.assertFalse(findAgentSystem.findSystem(client));
        Assert.assertTrue(findAgentSystem.findSystem(agent));
        Assert.assertFalse(findAgentSystem.findSystem(testAgent));
        Assert.assertFalse(findAgentSystem.findSystem(testAgent1));
        Assert.assertEquals(new CommandContainer("Stas", null, AnswerCode.NEW_CLIENT).toString(), answerAgent.toString());
        Assert.assertEquals(new CommandContainer("Vlad", null, AnswerCode.NEW_AGENT).toString(), answerClient.toString());
    }

}
