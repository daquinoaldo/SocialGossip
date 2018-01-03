import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SQLiteHelperTest {

    SQLiteHelper sqLiteHelper = new SQLiteHelper();

    @Test
    void init() {
        sqLiteHelper.init();
    }

    @Test
    void addUser() {
        // normal
        sqLiteHelper.addUser("username","password", "en");
        sqLiteHelper.addUser("daquinoaldo","password", "en");
        sqLiteHelper.addUser("zaphodias","password", "it");
        // with spaces
        sqLiteHelper.addUser("user name", "password", "es");
        // already existent: ERROR
        try {
            sqLiteHelper.addUser("username", "secret", "null");
        } catch (Exception e) {
            System.out.println("Exception!");
            System.out.println(e.getMessage());
        }   // TODO: non lancia eccezione, stampa errore ma passa il test
    }

    @Test
    void addRoom() {
        // normal
        sqLiteHelper.addRoom("Serie A", "zaphodias");
        sqLiteHelper.addRoom("Serie B", "daquinoaldo");
        // non-existent user: ERROR
        sqLiteHelper.addRoom("Stranger Things", "nobody");
    }

    @Test
    void getPassword() {
        // normal
        assertEquals(sqLiteHelper.getPassword("username"), "password");
        // non-existent user
        assertEquals(sqLiteHelper.getPassword("nobody"), null);
    }

    @Test
    void getCreator() {
    }

    @Test
    void addFriendship() {
        sqLiteHelper.addFriendship("zaphodias", "daquinoaldo");
        sqLiteHelper.addFriendship("zaphodias", "username");
        sqLiteHelper.addFriendship("daquinoaldo", "zaphodias");
        sqLiteHelper.addFriendship("daquinoaldo", "daquinoaldo");
    }

    @Test
    void checkFriendship() {
        sqLiteHelper.checkFriendship("zaphodias", "daquinoaldo");
    }

    @Test
    void getFriendships() {
        sqLiteHelper.getFriendships("zaphodias");
    }
}