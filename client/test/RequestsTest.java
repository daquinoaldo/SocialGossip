import base.Requests;
import base.State;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class RequestsTest {
    
    @Test
    void login() {
        assertThrows(IllegalArgumentException.class, () -> Requests.login(null, "provapass"));
        assertThrows(IllegalArgumentException.class, () -> Requests.login("provauser", null));
        assertThrows(IllegalArgumentException.class, () -> Requests.login("", "provapass"));
        assertThrows(IllegalArgumentException.class, () -> Requests.login("provauser", ""));
        
        // Test login to the server, with a timeout of 500 ms
        assertTimeout(Duration.ofMillis(500), () -> Requests.login("username", "password"));
        
        // check if State has been set up correctly
        assertEquals("username", State.username());
        assertTrue(State.isIsLoggedIn());
    }
}