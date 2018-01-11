package base;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.Test;

class EndpointsHandlerTest {
    
    @Test
    void login() {
        User stub = new User("stub");
        JSONObject params = new JSONObject();
        params.put("username", "username");
        params.put("password", "password");
        
        JSONObject reply = EndpointsHandler.login(stub, params);
        System.out.println("reply = " + reply);
    }
    
    @Test
    void register() {
        User stub = new User("stub");
        JSONObject params = new JSONObject();
        params.put("username", "username");
        params.put("password", "password");
        params.put("language", "it");
    
        JSONObject reply = EndpointsHandler.register(stub, params);
        System.out.println("reply = " + reply);
    
    }
}