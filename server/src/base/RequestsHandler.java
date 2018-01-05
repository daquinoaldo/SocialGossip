package base;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.HashMap;
import java.util.function.Function;

public class RequestsHandler {
    private static HashMap<String, Function<JSONObject, JSONObject>> endpoints = new HashMap<>();
    
    /* Register endpoint methods to the endpoint string */
    static {
        endpoints.put(
                Endpoints.LOGIN, EndpointsHandler::login
        );
        endpoints.put(
                Endpoints.REGISTER, EndpointsHandler::register
        );
        endpoints.put(
                Endpoints.LOOKUP, EndpointsHandler::lookup
        );
        endpoints.put(
                Endpoints.FRIENDSHIP, EndpointsHandler::friendship
        );
        endpoints.put(
                Endpoints.LIST_FRIEND, EndpointsHandler::listFriend
        );
        endpoints.put(
                Endpoints.CREATE_ROOM, EndpointsHandler::createRoom
        );
        endpoints.put(
                Endpoints.ADD_ME, EndpointsHandler::addMe
        );
        endpoints.put(
                Endpoints.CHAT_LIST, EndpointsHandler::chatList
        );
        endpoints.put(
                Endpoints.CLOSE_ROOM, EndpointsHandler::closeRoom
        );
        endpoints.put(
                Endpoints.FILE2FRIEND, EndpointsHandler::file2friend
        );
        endpoints.put(
                Endpoints.MSG2FRIEND, EndpointsHandler::msg2friend
        );
        endpoints.put(
                Endpoints.CHATROOM_MESSAGE, EndpointsHandler::chatroomMessage
        );
    }
    
    /* DISPATCHER */
    /**
     * Parse a JSON String into a JSONObject, then apply the method registered for the endpoint specified in the JSON,
     * if any.
     * Returns the result of the operation as a JSONObject, can be an error.
     * @param input String in valid JSON format.
     * @return A JSON String to be sent as reply containing the result of the operation, can be an error.
     */
    public static String parseRequest(String input) {
        try {
            // Parse JSON string into a JSONObject
            JSONParser parser = new JSONParser();
            JSONObject parsed = (JSONObject) parser.parse(input);
            
            // Read endpoint and parameters
            String endpoint = (String) parsed.get("endpoint");
            JSONObject params = (JSONObject) parsed.get("params");
            
            // Call the endpoint method associated if any, else throw exception
            if (endpoints.containsKey(endpoint)) {
                return endpoints.get(endpoint).apply(params).toJSONString();
            }
            else {
                System.err.println("Got an invalid request endpoint: " + endpoint);
                return buildErrorReply(404, "Endpoint not found.").toJSONString();
            }
        }
        catch (ParseException e) {
            System.err.println("Got an invalid JSON:\n" + input);
            e.printStackTrace();
    
            return buildErrorReply(400, "Invalid JSON request.").toJSONString();
        }
    }
    
    /* Helpers */
    static JSONObject buildSuccessReply() {
        JSONObject reply = new JSONObject();
        reply.put("status", "ok");
        reply.put("code", 200);
        return reply;
    }
    
    static JSONObject buildSuccessReply(JSONObject payload) {
        JSONObject baseReply = buildSuccessReply();
        baseReply.put("result", payload);
        return baseReply;
    }
    
    static JSONObject buildErrorReply(int statusCode, String message) {
        JSONObject reply = new JSONObject();
        reply.put("status", "err");
        reply.put("code", statusCode);
        reply.put("message", message);
        
        return reply;
    }
}
