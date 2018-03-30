//package ConsolePart.test;
//
//import model.User;
//import ConsolePart.SocketHandler;
//import model.FindAgentSystem;
//import org.junit.*;
//
//import static org.mockito.Mockito.mock;
//
//public class InMemoryDatabaseTest {
//    private static FindAgentSystem findAgentSystem;
//    private User client;
//    private User agent;
//    private SocketHandler socketHandlerAgent;
//    private SocketHandler socketHandlerClient;
//
//    @BeforeClass
//    public static void createFindAgentSystem(){
//        findAgentSystem=FindAgentSystem.getInstance();
//    }
//
//    @Before
//    public void init() {
//        findAgentSystem.createDatabase();
//        socketHandlerAgent = mock(SocketHandler.class);
//        socketHandlerClient = mock(SocketHandler.class);
//    }
//
//    @After
//    public void finaly() {
//        socketHandlerAgent = null;
//        socketHandlerClient = null;
//        client = null;
//        agent = null;
//        findAgentSystem.clear();
//        findAgentSystem.dropDatabase();
//    }
//
//    @Test
//    public void addClientOnDatabase() {
//        client = new User("stas", socketHandlerClient, false,null);
//        findAgentSystem.addClient(client);
//        findAgentSystem.remove(client);
//        Assert.assertTrue(findAgentSystem.findClient("stas"));
//    }
//
//    @Test
//    public void addAgentOnDatabase() {
//        agent = new User("stas", socketHandlerClient, true,null);
//        findAgentSystem.addAgent(agent);
//        findAgentSystem.remove(agent);
//        Assert.assertTrue(findAgentSystem.findAgent("stas"));
//    }
//
//    @Test
//    public void addClientOnDatabaseFindAgent() {
//        client = new User("stas", socketHandlerClient, false,null);
//        findAgentSystem.addClient(client);
//        findAgentSystem.remove(client);
//        Assert.assertFalse(findAgentSystem.findAgent("stas"));
//    }
//
//    @Test
//    public void addAgentOnDatabaseFindClient() {
//        agent = new User("stas", socketHandlerClient, true,null);
//        findAgentSystem.addAgent(agent);
//        findAgentSystem.remove(agent);
//        Assert.assertFalse(findAgentSystem.findClient("stas"));
//    }
//
//    @Test
//    public void loginDatabaseAgent() {
//        agent = new User("stas", socketHandlerClient, true,null);
//        findAgentSystem.addAgent(agent);
//        findAgentSystem.remove(agent);
//        Assert.assertTrue(findAgentSystem.authorize("stas", "Agent"));
//    }
//
//    @Test
//    public void loginDatabaseClient() {
//        client = new User("stas", socketHandlerClient, false,null);
//        findAgentSystem.addClient(client);
//        findAgentSystem.remove(client);
//        Assert.assertTrue(findAgentSystem.authorize("stas", "User"));
//    }
//
//}
//
//
