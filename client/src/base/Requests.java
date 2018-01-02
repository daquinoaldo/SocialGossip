package base;

import gui.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class Requests {
    private static JSONParser parser = new JSONParser();
    
    /* Methods */
    static void login(String username, String password) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        
        JSONObject reply = makeRequest(Endpoints.LOGIN, parameters);
        if (reply != null) {
            State.setLoggedIn(true);
            State.setUsername(username);
            JSONArray jsonFriends = (JSONArray) reply.get("friends");
            if (jsonFriends != null) {
                jsonFriends.forEach(friend -> {
                    String friendUsername = (String) ((JSONObject) friend).get("username");
                    State.addFriend(friendUsername);
                });
            }
        }
    }
    
    static void register() {
    
    }
    
    static void lookup() {
    
    }
    
    static void friendship() {
    
    }
    
    static void listFriends() {
    
    }
    
    /* Private helpers */
    private static boolean isReplyOk(JSONObject reply) {
        return reply != null && reply.get("status").equals("ok");
    }
    
    /**
     * Build a JSON to be sent to the server. Parameters (payload) can be included.
     *
     * @param endpoint, String representing the type of the request, must be specified
     * @param keyvalues, Map with key-value pairs to be included in the request
     * @return JSONObject with response, or null if an error occured
     * @throws IllegalArgumentException if an invalid endpoint is specified
     */
    private static JSONObject makeRequest(String endpoint, Map keyvalues) throws IllegalArgumentException {
        if (endpoint == null || endpoint.length() == 0)
            throw new IllegalArgumentException("Invalid endpoint specified.");
            
        JSONObject params = new JSONObject();
        params.putAll(keyvalues);
        
        JSONObject request = new JSONObject();
        request.put("endpoint", endpoint);
        request.put("params", params);
        
        // send request to the server and wait for a reply
        String reply = "{}";
        
        try {
            JSONObject response = (JSONObject) parser.parse(reply);
            
            if (isReplyOk(response))
                return response;
            else {
                Util.showErrorDialog((String) response.get("message"));
                return null;
            }
        }
        catch (ParseException e) {
            Util.showErrorDialog("Invalid JSON response from server:\n" + reply);
            System.err.println("Invalid JSON response from server:\n" + reply);
            return null;
        }
    }
}
