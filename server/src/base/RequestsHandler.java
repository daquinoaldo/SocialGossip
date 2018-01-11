package base;

import static base.Endpoints.*;

import connections.Multicast;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.BiFunction;

public class RequestsHandler {
    private static final HashMap<String, BiFunction<User, JSONObject, JSONObject>> endpoints = new HashMap<String, BiFunction<User, JSONObject, JSONObject>>(){{
        /* Register endpoint methods to the endpoint string */
        put(HEARTBEAT,          EndpointsHandler::heartbeat);
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
        put(MSG2FRIEND,         EndpointsHandler::msg2friend);
    }};
    
    private static final Database database = new Database();
    
    /* DISPATCHER */
    /**
     * Parse a JSON String into a JSONObject, then apply the method registered for the endpoint specified in the JSON,
     * if any.
     * Returns the result of the operation as a JSONObject, can be an error.
     * @param input String in valid JSON format.
     * @return A JSON String to be sent as reply containing the result of the operation, can be an error.
     */
    public static String parseRequest(User user, String input) {
        // Parse JSON string into a JSONObject
        JSONObject parsed = parse(input);
        if (parsed == null)
            return buildErrorReply(400, "Invalid JSON request.").toJSONString();
            
        // Read endpoint and parameters
        String endpoint = (String) parsed.get("endpoint");
        JSONObject params = (JSONObject) parsed.get("params");
        
        // Call the endpoint method associated if any, else throw exception
        if (endpoints.containsKey(endpoint)) {
            return endpoints.get(endpoint).apply(user, params).toJSONString();
        }
        else {
            System.err.println("Got an invalid request endpoint: " + endpoint);
            return buildErrorReply(404, "Endpoint not found.").toJSONString();
        }
    }

    public static void parseChatroomMessage(InetAddress senderAddress, String data) {
        JSONObject parsed = parse(data);
        JSONObject result = new JSONObject();
        
        if (parsed == null) {
            // Invalid json
            return;
        }
    
        String senderUsername = (String) parsed.get("sender");
        String chatroomName = (String) parsed.get("recipient");
        String text = (String) parsed.get("text");
        if (senderUsername == null || chatroomName == null || text == null || text.length() == 0)
            // Invalid request
            return;
        
        User sender = OnlineUsers.getByUsername(senderUsername);
        
        // Security check
        if (!senderAddress.getHostName().equals( sender.getPrimarySocket().getInetAddress().getHostName() )) {
            System.err.println("Sender stored data and real sender address mismatch.");
            return;
        }
    
        String broadcastIP = database.getBroadcastIP(chatroomName);
        if (broadcastIP == null) {
            // chatroom not found
            result.put("status", "err");
            result.put("message", "Chatroom not found");
            sender.sendMsgRequest(CHATROOM_MESSAGE, result);
            return;
        }
        
        ArrayList<String> subscribers = database.getChatSubscribers(chatroomName);
        int online = 0;
        for (String username : subscribers) {
            if (!username.equals(senderUsername) && OnlineUsers.isOnline(username))
                online++;
        }
        if (online == 0) {
            // nobody is online
            result.put("status", "err");
            result.put("recipient", chatroomName);
            result.put("message", "No user online at this time");
            sender.sendMsgRequest(CHATROOM_MESSAGE, result);
            return;
        }
    
        Multicast.broadcast(data, broadcastIP);
    }
    
    /* Helpers */
    private static JSONObject parse(String s) {
        try {
            // Parse JSON string into a JSONObject
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(s);
        }
        catch (ParseException e) {
            System.err.println("Invalid JSON: " + s);
            e.printStackTrace();
        }
        return null;
    }
    
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
