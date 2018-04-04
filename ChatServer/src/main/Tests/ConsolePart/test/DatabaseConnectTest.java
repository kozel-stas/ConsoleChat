package ConsolePart.test;

import model.DatabaseConnect;
import model.SupportClasses.Role;
import model.User;
import org.junit.*;

public class DatabaseConnectTest {
    private static DatabaseConnect databaseConnect;
    private User user;

    @BeforeClass
    public static void createDatabaseConnect() {
        databaseConnect = new DatabaseConnect();
    }

    @AfterClass
    public static void dropDatabaseConnect() {
        databaseConnect.dropDatabase();
    }

    @Before
    public void init() {
    }

    @After
    public void finaly() {
        user = null;
    }


    @Test
    public void addAndDeleteAgentOnDatabase() {
        user = new User("Stas", null, Role.AGENT, null);
        databaseConnect.addInDatabase(user);
        databaseConnect.removeFromDatabase(user);
        Assert.assertFalse(databaseConnect.findInDatabase(user));
    }

    @Test
    public void addAgentOnDatabase() {
        user = new User("Stas", null, Role.AGENT,null);
        databaseConnect.addInDatabase(user);
        Assert.assertTrue(databaseConnect.findInDatabase(user));
    }

    @Test
    public void FindAgentOnDatabase() {
        user = new User("Stas", null, Role.AGENT,null);
        Assert.assertFalse(databaseConnect.findInDatabase(user));
    }

    @Test
    public void addAndDeleteClientOnDatabase() {
        user = new User("Stas", null, Role.CLIENT,null);
        databaseConnect.addInDatabase(user);
        databaseConnect.removeFromDatabase(user);
        Assert.assertFalse(databaseConnect.findInDatabase(user));
    }

    @Test
    public void addClientOnDatabase() {
        user = new User("Stas", null, Role.CLIENT,null);
        databaseConnect.addInDatabase(user);
        Assert.assertTrue(databaseConnect.findInDatabase(user));
    }

    @Test
    public void FindClientOnDatabase() {
        user = new User("Stas", null, Role.CLIENT,null);
        Assert.assertFalse(databaseConnect.findInDatabase(user));
    }
}
