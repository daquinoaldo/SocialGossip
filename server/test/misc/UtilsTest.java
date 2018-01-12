package misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("SpellCheckingInspection")
class UtilsTest {
    
    @SuppressWarnings("ConstantConditions")
    @Test
    void md5() {
        // length
        assertEquals(32, Utils.md5("adsouèfhdsaèofhèoa").length());
        assertEquals(32, Utils.md5("a").length());
        assertEquals(32, Utils.md5("bbbbbbbbbbbbbbbb").length());
        assertEquals(32, Utils.md5("ciao").length());
        assertEquals(32, Utils.md5("").length());
        
        // value
        assertEquals("6e6bc4e49dd477ebc98ef4046c067b5f", Utils.md5("ciao"));
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", Utils.md5(""));
    }
    
    @Test
    void nextBroadcastIP() {
        assertEquals("239.225.225.226", Utils.nextBroadcastIP("239.225.225.225"));
        assertEquals("239.225.226.0", Utils.nextBroadcastIP("239.225.225.255"));
        assertNull(Utils.nextBroadcastIP("239.255.255.255"));
    }
}