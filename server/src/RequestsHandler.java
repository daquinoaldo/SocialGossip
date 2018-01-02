import base.Endpoints;
import exceptions.InvalidEndpointException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.function.Consumer;

public class RequestsHandler {
    private static JSONParser parser = new JSONParser();
    private static HashMap<String, Consumer<JSONObject>> endpoints = new HashMap<>();
    
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
    private static void login(JSONObject params) {
    
    }
    
    private static void register(JSONObject params) {
    
    }
    
    /* DISPATCHER */
    public static void parseRequest(String jsonString) throws InvalidEndpointException {
        try {
            // Parse JSON string into a JSONObject
            JSONObject parsed = (JSONObject) parser.parse(jsonString);
            
            // Read endpoint and parameters
            String endpoint = (String) parsed.get("endpoint");
            JSONObject params = (JSONObject) parsed.get("params");
            
            // Call the endpoint method associated if any, else throw exception
            if (endpoints.containsKey(endpoint)) {
                endpoints.get(endpoint).accept(params);
            }
            else {
                throw new InvalidEndpointException(endpoint);
            }
        }
        catch (ParseException e) {
            System.err.println("Unable to parse JSON request:");
            System.err.println(jsonString);
        }
    }
}
