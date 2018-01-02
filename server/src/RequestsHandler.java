import base.Endpoints;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class RequestsHandler {
    private static HashMap<String, Function<JSONObject, JSONObject>> endpoints = new HashMap<>();
    
    /* Register endpoint methods to the endpoint string */
    static {
        endpoints.put(
          Endpoints.LOGIN, RequestsHandler::login
        );
        endpoints.put(
          Endpoints.REGISTER, RequestsHandler::register
        );
    }
    
    /* ENDPOINTS */
    private static JSONObject login(JSONObject params) {
        return buildErrorReply(400, "Not implemented");
    }
    
    private static JSONObject register(JSONObject params) {
        return buildErrorReply(400, "Not implemented");
    }
    
    /* DISPATCHER */
    public static JSONObject parseRequest(String input) {
        try {
            // Parse JSON string into a JSONObject
            JSONParser parser = new JSONParser();
            JSONObject parsed = (JSONObject) parser.parse(input);
            
            // Read endpoint and parameters
            String endpoint = (String) parsed.get("endpoint");
            JSONObject params = (JSONObject) parsed.get("params");
            
            // Call the endpoint method associated if any, else throw exception
            if (endpoints.containsKey(endpoint)) {
                return endpoints.get(endpoint).apply(params);
            }
            else {
                System.err.println("Invalid endpoint specified: " + endpoint);
            }
        }
        catch (ParseException e) {
            System.err.println("Got an invalid JSON.");
            e.printStackTrace();
        }
        
        return null;
    }
    
    /* Helpers */
    private static JSONObject buildSuccessReply() {
        JSONObject reply = new JSONObject();
        reply.put("status", "ok");
        reply.put("code", 200);
        return reply;
    }
    
    private static JSONObject buildSuccessReply(JSONObject payload) {
        JSONObject baseReply = buildSuccessReply();
        baseReply.put("result", payload);
        return baseReply;
    }
    
    private static JSONObject buildErrorReply(int statusCode, String message) {
        JSONObject reply = new JSONObject();
        reply.put("status", "err");
        reply.put("code", statusCode);
        reply.put("message", message);
        
        return reply;
    }
}
