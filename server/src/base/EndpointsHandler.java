package base;

import org.json.simple.JSONObject;

import static base.RequestsHandler.buildErrorReply;
import static base.RequestsHandler.buildSuccessReply;

public class EndpointsHandler {
    private static Database db = new Database();
    
    static {
        db.init();
    }
    
    static JSONObject login(JSONObject params) {
        return buildErrorReply(400, "Not implemented");
    }
    
    static JSONObject register(JSONObject params) {
        return buildErrorReply(400, "Not implemented");
    }
    
    /* Helpers */
    private static boolean checkPassword(String username, String password) {
        String realPasswordHash = db.getPassword(username);
        String actualPasswordHash = Utils.md5(password);
        return realPasswordHash.equals(actualPasswordHash);
    }
}
