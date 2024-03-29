package connections;

import static misc.Endpoints.*;

import misc.Database;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import state.OnlineUsers;
import state.User;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

class RequestsHandler {
    private static final HashMap<String, BiFunction<User, JSONObject, JSONObject>> primaryEndpoints = new HashMap<String, BiFunction<User, JSONObject, JSONObject>>(){{
        /* Register endpoint methods to the endpoint string */
        put(LOGIN,              EndpointsHandler::login);
        put(REGISTER,           EndpointsHandler::register);
        put(LOOKUP,             EndpointsHandler::lookup);
        put(FRIENDSHIP,         EndpointsHandler::friendship);
        put(IS_ONLINE,          EndpointsHandler::isOnline);
        put(LIST_FRIEND,        EndpointsHandler::listFriend);
        put(CREATE_ROOM,        EndpointsHandler::createRoom);
        put(ADD_ME,             EndpointsHandler::addMe);
        put(CHAT_LIST,          EndpointsHandler::chatList);
        put(CLOSE_ROOM,         EndpointsHandler::closeRoom);
        put(FILE2FRIEND,        EndpointsHandler::file2friend);
    }};
    
    private static final HashMap<String, BiFunction<User, JSONObject, JSONObject>> messageEndpoints = new HashMap<String, BiFunction<User, JSONObject, JSONObject>>(){{
        put(HEARTBEAT,          EndpointsHandler::heartbeat);
        put(LOGIN,              EndpointsHandler::login);
        put(FILE2FRIEND,        EndpointsHandler::file2friend);
        put(MSG2FRIEND,         EndpointsHandler::msg2friend);
    }};
    
    private static final Database database = new Database();
    
    /* DISPATCHER */
    /**
     * Parse a JSON String into a JSONObject, then apply the method registered for the endpoint specified in the JSON,
     * if any. This is for the primary connection only.
     * Returns the result of the operation as a JSONObject, can be an error.
     * @param input String in valid JSON format.
     * @return A JSON String to be sent as reply containing the result of the operation, can be an error.
     */
    public static String parseRequest(User user, String input) {
        return parseGenericRequest(user, input, true);
    }
    
    /**
     * Parse a JSON String into a JSONObject, then apply the method registered for the endpoint specified in the JSON,
     * if any. This is for the message connection only.
     * Returns the result of the operation as a JSONObject, can be an error.
     * @param input String in valid JSON format.
     * @return A JSON String to be sent as reply containing the result of the operation, can be an error.
     */
    public static String parseMessageRequest(User user, String input) {
        return parseGenericRequest(user, input, false);
    }
    
    private static String parseGenericRequest(User user, String input, boolean isPrimaryRequest) {
        // Parse JSON string into a JSONObject
        JSONObject parsed = parse(input);
        if (parsed == null)
            return buildErrorReply(400, "Invalid JSON request.").toJSONString();
    
        // Read endpoint and parameters
        String endpoint = (String) parsed.get("endpoint");
        JSONObject params = (JSONObject) parsed.get("params");
    
        // Call the endpoint method associated if any, else throw exception
        HashMap<String, BiFunction<User, JSONObject, JSONObject>> endpoints = isPrimaryRequest ?
                        primaryEndpoints :
                        messageEndpoints ;
        
        if (endpoints.containsKey(endpoint)) {
            JSONObject response = endpoints.get(endpoint).apply(user, params);
            if (response != null)
                return response.toJSONString();
            else return null;
        }
        else {
            System.err.println("Got an invalid request endpoint: " + endpoint);
            return buildErrorReply(404, "Endpoint not found.").toJSONString();
        }
    }

    /**
     * Parse a JSON String into a JSONObject, then send it in the room multicast address
     * @param senderAddress InetAddress of the sender
     * @param data the stringified json message
     */
    @SuppressWarnings("unchecked")
    public static void parseRoomMessage(InetAddress senderAddress, String data) {
        JSONObject parsed = parse(data);
        JSONObject result = new JSONObject();
        
        if (parsed == null) {
            // Invalid json
            return;
        }
    
        String senderUsername = (String) parsed.get("sender");
        String roomName = (String) parsed.get("recipient");
        String text = (String) parsed.get("text");
        if (senderUsername == null || roomName == null || text == null || text.length() == 0)
            // Invalid request
            return;
        
        User sender = OnlineUsers.getByUsername(senderUsername);
        
        // Security check
        if (!senderAddress.getHostName().equals( sender.getPrimarySocket().getInetAddress().getHostName() )) {
            System.err.println("Sender stored data and real sender address mismatch.");
            return;
        }
    
        String broadcastIP = database.getBroadcastIP(roomName);
        if (broadcastIP == null) {
            // room not found
            result.put("status", "err");
            result.put("message", "Room not found");
            sender.sendMsgRequest(ROOM_MESSAGE, result);
            return;
        }
        
        ArrayList<String> subscribers = database.getRoomSubscribers(roomName);
        int online = 0;
        for (String username : subscribers) {
            if (!username.equals(senderUsername) && OnlineUsers.isOnline(username))
                online++;
        }
        if (online == 0) {
            // nobody is online
            result.put("status", "err");
            result.put("recipient", roomName);
            result.put("message", "No user online at this time");
            sender.sendMsgRequest(ROOM_MESSAGE, result);
            return;
        }
    
        Multicast.broadcast(data, broadcastIP);
    }
    
    /* Helpers */

    /**
     * Parse a json string into a JSONObject
     * @param s the json String to be parsed
     * @return the parsed JSONObject
     */
    private static JSONObject parse(String s) {
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(s);
        }
        catch (ParseException e) {
            System.err.println("Invalid JSON: " + s);
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Builds default replies
     * @return JSONObject
     */

    @SuppressWarnings("unchecked")
    static JSONObject buildSuccessReply() {
        JSONObject reply = new JSONObject();
        reply.put("status", "ok");
        reply.put("code", 200);
        return reply;
    }
    
    @SuppressWarnings("unchecked")
    static JSONObject buildSuccessReply(JSONObject payload) {
        JSONObject baseReply = buildSuccessReply();
        baseReply.put("result", payload);
        return baseReply;
    }

    @SuppressWarnings("unchecked")
    static JSONObject buildErrorReply(int statusCode, String message) {
        JSONObject reply = new JSONObject();
        reply.put("status", "err");
        reply.put("code", statusCode);
        reply.put("message", message);
        return reply;
    }
}
