package base;

import Connections.Connection;
import gui.Utils;
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
    //TODO: ma al login è necessario comunicare amici e chat? Non sarebbe più oppurtuno che il client usasse le apposite funzioni?
    @SuppressWarnings("unchecked")
    public static void login(String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0)
            throw new IllegalArgumentException("Username and password must be a non-empty string.");
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        
        JSONObject reply = makeRequest(Endpoints.LOGIN, parameters);
        if (reply != null) {
            State.setLoggedIn(true);
            State.setUsername(username);
            JSONArray jsonFriends = (JSONArray) reply.get("friends");
            //TODO: ricontrollare, c'è da segnalare anche lo stato (online/offline)
            if (jsonFriends != null)
                jsonFriends.forEach(friend -> {
                    String friendUsername = (String) ((JSONObject) friend).get("username");
                    State.addFriend(friendUsername);
                });
        }
    }
    
    public static boolean register(String username, String password, String language) {
        if (username == null || password == null || language == null ||
                username.length() == 0 || password.length() == 0 || language.length() == 0)
            throw new IllegalArgumentException("Username, password and language must be a non-empty string.");
    
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("language", language);

        JSONObject reply = makeRequest(Endpoints.REGISTER, parameters);
        return isReplyOk(reply);
    }
    
    public static boolean lookup(String username) {
        if (username == null || username.length() == 0)
            throw new IllegalArgumentException("Username must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        JSONObject reply = makeRequest(Endpoints.LOOKUP, parameters);
        return isReplyOk(reply);
    }
    
    public static boolean friendship(String username) {
        if (username == null || username.length() == 0)
            throw new IllegalArgumentException("Username must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        JSONObject reply = makeRequest(Endpoints.FRIENDSHIP, parameters);
        return isReplyOk(reply);
    }
    
    public static void listFriends() {
        JSONObject reply = makeRequest(Endpoints.LIST_FRIEND, null);
        //TODO: reply
    }

    public static boolean createRoom(String room) {
        if (room == null || room.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", room);
        JSONObject reply = makeRequest(Endpoints.CREATE_ROOM, parameters);
        return isReplyOk(reply);
    }

    public static boolean addMe(String room) {
        if (room == null || room.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", room);
        JSONObject reply = makeRequest(Endpoints.ADD_ME, parameters);
        return isReplyOk(reply);
    }

    public static void chatList() {
        JSONObject reply = makeRequest(Endpoints.CHAT_LIST, null);
        //TODO: reply
    }

    public static boolean closeRoom(String room) {
        if (room == null || room.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", room);
        JSONObject reply = makeRequest(Endpoints.CLOSE_ROOM, parameters);
        return isReplyOk(reply);
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
     * @param keyvalues, Map with key-value pairs to be included in the request, can be null
     * @return JSONObject with response, or null if an error occured
     * @throws IllegalArgumentException if an invalid endpoint is specified
     */
    @SuppressWarnings("unchecked")
    private static JSONObject makeRequest(String endpoint, Map keyvalues) throws IllegalArgumentException {
        if (endpoint == null || endpoint.length() == 0)
            throw new IllegalArgumentException("Invalid endpoint specified.");
        
        JSONObject params = new JSONObject();
        if(keyvalues != null) params.putAll(keyvalues); // it can be null if the request has no parameters
        
        JSONObject request = new JSONObject();
        request.put("endpoint", endpoint);
        request.put("params", params);
        
        
        // send request to the server and wait for a reply
        String responseString = Connection.sendRequest(request.toJSONString());
        
        if (responseString == null) {
            Utils.showErrorDialog("Impossibile comunicare con il server. Controllare la connessione a internet e riprovare.");
            return null;
        }
    
    
        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(responseString);
            
            if (isReplyOk(response))
                return response;
            else {
                String msg = (String) response.get("message");
                Utils.showErrorDialog(msg != null && msg.length() > 0 ? msg : "Errore sconosciuto.");
                return null;
            }
        }
        catch (ParseException e) {
            Utils.showErrorDialog("Invalid JSON response from server");
            System.err.println("Invalid JSON response from server: \n" + responseString);
            e.printStackTrace();
        }
        return null;
    }
}
