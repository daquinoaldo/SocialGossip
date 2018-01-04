import base.Database;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DatabaseTest {

    Database database = new Database("testdatabase.db");
    
    private void resetDatabase() {
        try { Files.delete(Paths.get("testdatabase.db")); }
        catch (Exception e) { }
        database.init();
    }
    
    @Test
    void addUser() {
        resetDatabase();
        
        assertTrue(database.addUser("username", "password", "en"));
        assertTrue(database.addUser("daquinoaldo","password", "en"));
        assertTrue(database.addUser("zaphodias","password", "it"));
        // with spaces
        assertTrue(database.addUser("user name", "password", "es"));
        
        // already existent
        assertFalse(database.addUser("username", "secret", "null"));
    }

    @Test
    void addRoom() {
        resetDatabase();
        database.addUser("zaphodias", "password", "en");
        database.addUser("daquinoaldo","password", "en");
        
        assertTrue(database.addRoom("Serie A", "zaphodias"));
        assertTrue(database.addRoom("Serie B", "daquinoaldo"));
        
        assertFalse(database.addRoom("Stranger Things", "nobody"));
    }

    @Test
    void getPassword() {
        resetDatabase();
        database.addUser("username", "password", "en");
        
        assertEquals("password", database.getPassword("username"));

        // non-existent user
        assertNull(database.getPassword("asdasdasddas"));
    }

    @Test
    void getCreator() {
        resetDatabase();
        database.addUser("username", "password", "en");
        database.addRoom("Serie A", "username");
    
        assertEquals("username", database.getCreator("Serie A"));
    }

    @Test
    void addFriendship() {
        resetDatabase();
        database.addUser("username", "password", "en");
        database.addUser("zaphodias", "password", "en");
        database.addUser("daquinoaldo", "password", "en");
    
        assertTrue(database.addFriendship("zaphodias", "daquinoaldo"));
        assertTrue(database.addFriendship("zaphodias", "username"));
        assertFalse(database.addFriendship("daquinoaldo", "zaphodias"));
        assertFalse(database.addFriendship("daquinoaldo", "daquinoaldo"));
    }

    @Test
    void checkFriendship() {
        resetDatabase();
        database.addUser("zaphodias", "password", "en");
        database.addUser("daquinoaldo", "password", "en");
        database.addFriendship("zaphodias", "daquinoaldo");
        
        assertTrue(database.checkFriendship("zaphodias", "daquinoaldo"));
    }

    @Test
    void getFriendships() {
        resetDatabase();
        database.addUser("zaphodias", "password", "en");
        database.addUser("daquinoaldo", "password", "en");
        database.addUser("username", "password", "en");
        database.addFriendship("zaphodias", "daquinoaldo");
        database.addFriendship("username", "zaphodias");
    
        ArrayList<String> expectedFriends = new ArrayList<>();
        expectedFriends.add("daquinoaldo");
        expectedFriends.add("username");
        
        ArrayList<String> actualFriends = database.getFriendships("zaphodias");
        
        assertLinesMatch(expectedFriends, actualFriends);
    }
}