import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JsonTest {
    @Test
    void SimpleJsonTest() {
        JSONObject jsonObj = new JSONObject();
        jsonObj.put("key", "value");
        
        assertEquals("{\"key\":\"value\"}", jsonObj.toJSONString(), "JSON creation and toJSONString");
    }
}
