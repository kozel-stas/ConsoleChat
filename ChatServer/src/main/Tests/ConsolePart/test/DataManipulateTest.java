package ConsolePart.test;

import ConsolePart.SocketHandler;
import model.*;
import org.junit.*;

import static org.mockito.Mockito.mock;

public class DataManipulateTest {
    private static DataManipulate dataManipulate;
    private SocketHandler socketHandler;
    private User user;

    @BeforeClass
    public static void createDatabaseConnect() {
        dataManipulate = DataManipulate.getInstance();
    }

    @Before
    public void init(){
        socketHandler=mock(SocketHandler.class);
    }

    @After
    public void finaly() {
        dataManipulate.clear();
        user = null;
    }


    @Test
    public void addAgent() {
        user = new User("Stas0", null, Role.AGENT, null);
        dataManipulate.add(user);
        Assert.assertTrue(dataManipulate.find(user));
    }

    @Test
    public void addClient() {
        user = new User("Stas1", null, Role.CLIENT, null);
        dataManipulate.add(user);
        Assert.assertTrue(dataManipulate.find(user));
    }

    @Test
    public void addandDelAgent() {
        user = new User("Stas2", null, Role.AGENT, null);
        dataManipulate.add(user);
        dataManipulate.remove(user);
        Assert.assertFalse(dataManipulate.find(user));
    }

    @Test
    public void addandDelClient() {
        user = new User("Stas3", null, Role.CLIENT, null);
        dataManipulate.add(user);
        dataManipulate.remove(user);
        Assert.assertFalse(dataManipulate.find(user));
    }

    @Test
    public void loginAgent(){
        user = new User("Stas4", null, Role.AGENT, null);
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_AGENT).toString());
    }

    @Test
    public void loginClient(){
        user = new User("Stas5", null, Role.CLIENT, null);
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Server", null, AnswerCode.DONT_HAVE_REGISTER_CLIENT).toString());
    }

    @Test
    public void registerAgent(){
        user=new User("Stas6",socketHandler,Role.AGENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas6",Role.AGENT,AnswerCode.GOOD_REGISTER).toString());
    }

    @Test
    public void registerClient(){
        user=new User("Stas7",socketHandler,Role.CLIENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas7",Role.CLIENT,AnswerCode.GOOD_REGISTER).toString());
    }

    @Test
    public void loginAfterRegisterAgent(){
        user=new User("Stas8",socketHandler,Role.AGENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas8",Role.AGENT,AnswerCode.GOOD_REGISTER).toString());
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Server",null,AnswerCode.AGENT_ONLINE_YET).toString());
    }

    @Test
    public void loginAfterRegisterClient(){
        user=new User("Stas9",socketHandler,Role.CLIENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas9",Role.CLIENT,AnswerCode.GOOD_REGISTER).toString());
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Server",null,AnswerCode.CLIENT_ONLINE_YET).toString());
    }

    @Test
    public void registerAfterRegisterAgent(){
        user=new User("Stas10",socketHandler,Role.AGENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas10",Role.AGENT,AnswerCode.GOOD_REGISTER).toString());
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Server",null,AnswerCode.NAME_ALREADY_USED).toString());
    }

    @Test
    public void registerAfterRegisterClient(){
        user=new User("Stas11",socketHandler,Role.CLIENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas11",Role.CLIENT,AnswerCode.GOOD_REGISTER).toString());
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Server",null,AnswerCode.NAME_ALREADY_USED).toString());
    }

    @Test
    public void loginAfterLeaveAgent(){
        user=new User("Stas12",socketHandler,Role.AGENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas12",Role.AGENT,AnswerCode.GOOD_REGISTER).toString());
        dataManipulate.remove(user);
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Stas12",Role.AGENT,AnswerCode.GOOD_LOGIN).toString());
    }

    @Test
    public void loginAfterLeaveClient(){
        user=new User("Stas13",socketHandler,Role.CLIENT,null);
        Assert.assertEquals(dataManipulate.register(user).toString(),new CommandContainer("Stas13",Role.CLIENT,AnswerCode.GOOD_REGISTER).toString());
        dataManipulate.remove(user);
        Assert.assertEquals(dataManipulate.login(user).toString(),new CommandContainer("Stas13",Role.CLIENT,AnswerCode.GOOD_LOGIN).toString());
    }
    
}
