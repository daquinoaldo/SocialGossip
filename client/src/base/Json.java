package base;

import Connections.Connection;
import gui.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.Endpoints.*;

public class Json {
    public static void parseMessageRequest(String jsonString) {
        try {
            JSONParser parser = new JSONParser();
            JSONObject request = (JSONObject) parser.parse(jsonString);
            JSONObject payload = (JSONObject) request.get("params");
            
            String endpoint = (String) request.get("endpoint");
            String status = (String) request.get("status");
            
            if (endpoint == null) {
                // not a request
                return;
            }
            
            switch (endpoint) {
                case MSG2FRIEND:
                    // Got a message from friend
                    String username = (String) payload.get("username");
                    String text = (String) payload.get("text");
                    State.Message msg = new State.Message(username, text);
                    // TODO: update State    - State.newPrivateMessage(username, msg)
                    break;
                    
                case FILE2FRIEND:
                    // Got a request to send a file to me
                    String fromUsername = (String) payload.get("username");
                    // if (!State.isFriend(fromUsername)) return;
    
                    // aprire dialog di conferma
                    // aprire dialog di selezione destinazione
    
                    // int port = Connection.receiveFile();
                    JSONObject reply = new JSONObject();
                    reply.put("status", "ok");
                    // reply.put("port", port);
                    Connection.sendMsgRequest(reply.toJSONString());
                    break;
                case HEARTBEAT:
                    JSONObject response = new JSONObject();
                    response.put("username", State.username());
                    //Connection.sendMsgRequest(response.toJSONString());
                    break;
            }
        }
        catch (ParseException e) {
            System.err.println("Invalid JSON message: ");
            System.err.println(jsonString);
            e.printStackTrace();
        }
    }
    
    /* Request builders */
    public static void heartbeat() {
        makeMsgRequest(HEARTBEAT, null);
    }
    
    @SuppressWarnings("unchecked")
    public static void login(String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0)
            throw new IllegalArgumentException("Username and password must be a non-empty string.");
        if(Utils.isDebug) System.out.println("Json.login()");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        
        JSONObject reply = makeRequest(LOGIN, parameters);
        if (reply == null)
            return;
        
        makeMsgRequest(LOGIN, parameters);
        
        State.setLoggedIn(true);
        State.setUsername(username);
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
    
    public static List<State.Friend> listFriends() {
        JSONObject reply = makeRequest(Endpoints.LIST_FRIEND, null);
        if(!isReplyOk(reply)) return null;
        JSONArray jsonArray = (JSONArray) reply.get("friends");
        List<State.Friend> friends = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            String username = (String) ((JSONObject) jsonObject).get("username");
            boolean online = (boolean) ((JSONObject) jsonObject).get("online");
            friends.add(new State.Friend(username, online));
        }
        return friends;
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

    public static List<State.Room> chatList() {
        JSONObject reply = makeRequest(Endpoints.CHAT_LIST, null);
        if(!isReplyOk(reply)) return null;
        JSONArray jsonArray = (JSONArray) reply.get("rooms");
        List<State.Room> rooms = new ArrayList<>();
        for (Object jsonObject : jsonArray) {
            String name = (String) ((JSONObject) jsonObject).get("name");
            boolean added = (boolean) ((JSONObject) jsonObject).get("added");
            rooms.add(new State.Room(name, added));
        }
        return rooms;
    }

    public static boolean closeRoom(String room) {
        if (room == null || room.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", room);
        JSONObject reply = makeRequest(Endpoints.CLOSE_ROOM, parameters);
        return isReplyOk(reply);
    }

    //TODO: Pitasi ricontrolla!
    public static boolean sendMsg(String recipient, String text) {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("sender", State.username());
        parameters.put("recipient", recipient);
        parameters.put("text", text);
        JSONObject reply = makeRequest(Endpoints.MSG2FRIEND, parameters);
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
        return makeGenericRequest(endpoint, keyvalues, false);
    }

    private static void makeMsgRequest(String endpoint, Map keyvalues) throws IllegalArgumentException {
        makeGenericRequest(endpoint, keyvalues, true);
    }

    private static JSONObject makeGenericRequest(String endpoint, Map keyvalues, boolean isMsgRequest) throws IllegalArgumentException {
        if (endpoint == null || endpoint.length() == 0)
            throw new IllegalArgumentException("Invalid endpoint specified.");

        JSONObject params = new JSONObject();
        if(keyvalues != null) params.putAll(keyvalues); // it can be null if the request has no parameters

        JSONObject request = new JSONObject();
        request.put("endpoint", endpoint);
        request.put("params", params);


        // send request to the server using the right socket, and wait for a reply
        if(Utils.isDebug) System.out.println("Json.makeRequest: sending request...");
        
        if (isMsgRequest) {
            Connection.sendMsgRequest(request.toJSONString());
            return null;
        }
        
        String responseString = Connection.sendRequest(request.toJSONString());
        
        if(Utils.isDebug) System.out.println("Json.makeRequest: got response");

        if (responseString == null) {
            Utils.showErrorDialog("Impossibile comunicare con il server. Controllare la connessione a internet e riprovare.");
            return null;
        }

        try {
            JSONParser parser = new JSONParser();
            JSONObject response = (JSONObject) parser.parse(responseString);


            if (isReplyOk(response)) {
                if(Utils.isDebug) System.out.println("Json.makeRequest: reply ok!");
                return response;
            }
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
