package base;

import Connections.Connection;
import gui.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.Map;

public class Json {
    /* Json parsers to objects */
    public static State.Message parseMessage(String jsonString) {
        String username = null;
        String text = null;
        
        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(jsonString);
            username = (String) response.get("username");
            text = (String) response.get("text");
        }
        catch (ParseException e) {
            System.err.println("Invalid JSON message: ");
            System.err.println(jsonString);
            e.printStackTrace();
        }
        
        return new State.Message(username, text);
    }
    
    /* Request builders */
    public static void login(String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            throw new IllegalArgumentException("Username and password must be a non-empty string.");
        }
        
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
    
    public static boolean register(String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0) {
            throw new IllegalArgumentException("Username and password must be a non-empty string.");
        }
    
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
    
        JSONObject reply = makeRequest(Endpoints.REGISTER, parameters);
        return isReplyOk(reply);
    }
    
    public static void lookup() {
    
    }
    
    public static void friendship() {
    
    }
    
    public static void listFriends() {
    
    }
    
    /* Private helpers */
    
    /**
     * @return true if server reply doesn't contain errors, false otherwise
     */
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
        String responseString = Connection.sendRequest(request.toJSONString());
        
        if (responseString == null) {
            Util.showErrorDialog("Impossibile comunicare con il server. Controllare la connessione a internet e riprovare.");
            return null;
        }
    
    
        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(responseString);
            
            if (isReplyOk(response))
                return response;
            else {
                String msg = (String) response.get("message");
                Util.showErrorDialog(msg != null && msg.length() > 0 ? msg : "Errore sconosciuto.");
                return null;
            }
        }
        catch (ParseException e) {
            Util.showErrorDialog("Invalid JSON response from server");
            System.err.println("Invalid JSON response from server: \n" + responseString);
            e.printStackTrace();
        }
        return null;
    }
}
