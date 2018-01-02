package base;

import gui.Util;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class Requests {
    /* Methods */
    public static void login(String username, String password) {
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
    
    public static void register() {
    
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
        // move this to his own class
        String responseString = "";
        try {
            Socket socket = new Socket("localhost", Configuration.PRIMARY_PORT);
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            writer.write(request.toJSONString());
            writer.newLine();
            writer.flush();
    
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            responseString = reader.readLine();
            reader.close();
            // end connection
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    
    
        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(responseString);
            
            if (isReplyOk(response))
                return response;
            else {
                Util.showErrorDialog((String) response.get("message"));
                return null;
            }
        }
        catch (ParseException e) {
            Util.showErrorDialog("Invalid JSON response from server");
            System.err.println("Invalid JSON response from server");
            e.printStackTrace();
        }
        return null;
    }
}
