package ConsolePart.test;

import model.Client;
import ConsolePart.SocketHandler;
import model.FindAgentSystem;
import org.junit.*;

import static org.mockito.Mockito.mock;

public class InMemoryDatabaseTest {
    private static FindAgentSystem findAgentSystem;
    private Client client;
    private Client agent;
    private SocketHandler socketHandlerAgent;
    private SocketHandler socketHandlerClient;

    @BeforeClass
    public static void createFindAgentSystem(){
        findAgentSystem=new FindAgentSystem();
    }

    @Before
    public void init() {
        findAgentSystem.createDatabase();
        socketHandlerAgent = mock(SocketHandler.class);
        socketHandlerClient = mock(SocketHandler.class);
    }

    @After
    public void finaly() {
        socketHandlerAgent = null;
        socketHandlerClient = null;
        client = null;
        agent = null;
        findAgentSystem.clear();
        findAgentSystem.dropDatabase();
    }

    @Test
    public void addClientOnDatabase() {
        client = new Client("stas", socketHandlerClient, false);
        findAgentSystem.addUser(client);
        findAgentSystem.removeUser(client);
        Assert.assertTrue(findAgentSystem.findUser("stas"));
    }

    @Test
    public void addAgentOnDatabase() {
        agent = new Client("stas", socketHandlerClient, true);
        findAgentSystem.addAgent(agent);
        findAgentSystem.removeAgent(agent);
        Assert.assertTrue(findAgentSystem.findAgent("stas"));
    }

    @Test
    public void addClientOnDatabaseFindAgent() {
        client = new Client("stas", socketHandlerClient, false);
        findAgentSystem.addUser(client);
        findAgentSystem.removeUser(client);
        Assert.assertFalse(findAgentSystem.findAgent("stas"));
    }

    @Test
    public void addAgentOnDatabaseFindClient() {
        agent = new Client("stas", socketHandlerClient, true);
        findAgentSystem.addAgent(agent);
        findAgentSystem.removeAgent(agent);
        Assert.assertFalse(findAgentSystem.findUser("stas"));
    }

    @Test
    public void loginDatabaseAgent() {
        agent = new Client("stas", socketHandlerClient, true);
        findAgentSystem.addAgent(agent);
        findAgentSystem.removeAgent(agent);
        Assert.assertTrue(findAgentSystem.login("stas", "Agent"));
    }

    @Test
    public void loginDatabaseClient() {
        client = new Client("stas", socketHandlerClient, true);
        findAgentSystem.addUser(client);
        findAgentSystem.removeUser(client);
        Assert.assertTrue(findAgentSystem.login("stas", "Client"));
    }

}


