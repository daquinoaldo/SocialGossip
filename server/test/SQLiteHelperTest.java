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
    void addMessage() {
        // creator
        sqLiteHelper.addMessage("Serie A", "zaphodias", "Message");
        // another user (with spaces)
        sqLiteHelper.addMessage("Serie A", "daquinoaldo", "Another message...");
        // With special chars
        sqLiteHelper.addMessage("Serie B", "username", "Invisible-M€ssage");
        // non-existent user: ERROR
        sqLiteHelper.addMessage("Serie A", "nobody", "Something strange");
        // non-existent room: ERROR
        sqLiteHelper.addMessage("invisible r00m", "username", "Invisible-M€ssage");
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
    void getMessage() {
    }

    @Test
    void addFriendship() {
        sqLiteHelper.addFriendship("zaphodias", "daquinoaldo");
        sqLiteHelper.addFriendship("daquinoaldo", "zaphodias");
        sqLiteHelper.addFriendship("daquinoaldo", "daquinoaldo");
    }

    @Test
    void checkFriendship() {
        sqLiteHelper.checkFriendship("zaphodias", "daquinoaldo");
    }
}