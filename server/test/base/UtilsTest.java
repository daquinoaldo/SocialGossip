package base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UtilsTest {
    
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
        // TODO
    }
}