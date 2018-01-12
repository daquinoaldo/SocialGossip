package misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TranslationTest {
    @Test
    void translate() {
        assertEquals("Ciao Mondo", Translation.translate("Hello World", "en", "it"));
        assertEquals("Hi", Translation.translate("Ciao", "it", "EN"));
    
        assertThrows(IllegalArgumentException.class, () -> Translation.translate("", "it", "en"));
        assertThrows(IllegalArgumentException.class, () -> Translation.translate("sdf", "it", "aaaa"));
    }
}