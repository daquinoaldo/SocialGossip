package base;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.HashMap;
import java.util.List;

import static base.RequestsHandler.buildErrorReply;
import static base.RequestsHandler.buildSuccessReply;

public class EndpointsHandler {

    private static Database db = new Database();
    private static HashMap<String, User> onlineUsers = new HashMap<>();
    
    static {
        db.init();
    }

    /* Helpers */
    private static boolean checkPassword(String username, String password) {
        String realPasswordHash = db.getPassword(username);
        String actualPasswordHash = Utils.md5(password);
        return realPasswordHash.equals(actualPasswordHash);
    }
    
    static JSONObject login(JSONObject params) {
        String username = (String) params.get("username");
        if(!db.existUser(username)) return buildErrorReply(401, "base.User not exist");
        if(!checkPassword((String) params.get("password"), db.getPassword(username)))
            return buildErrorReply(401, "Wrong password");
        onlineUsers.put(username, new User(username));
        List<String> friends = db.getFriendships(username);
        for (String friend : friends)
            if (onlineUsers.containsKey(friend))
                ;//TODO: onlineUsers.get(friend).notifyChangeStatus(username);
        return buildSuccessReply();
    }
    
    static JSONObject register(JSONObject params) {
        String username = (String) params.get("username");
        if(db.existUser(username)) return buildErrorReply(400, "User already exists.");
        if(!db.addUser(username, Utils.md5((String) params.get("password")),
                (String) params.get("language"))) return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    static JSONObject lookup(JSONObject params) {
        if(!db.existUser((String) params.get("nickname")))
            return buildErrorReply(400, "User not exists.");
        return buildSuccessReply();
    }

    static JSONObject friendship(String currentUser, JSONObject params) {
        String nickname = (String) params.get("nickname");
        if(!db.existUser(nickname)) return buildErrorReply(400, "User not exists.");
        if(!db.addFriendship(currentUser, nickname))
            return buildErrorReply(400, "Database error.");
        if (onlineUsers.containsKey(nickname))
            ;//TODO: onlineUsers.get(nickname).notifyFriendship(currentUser)
        return buildSuccessReply();
    }

    @SuppressWarnings("unchecked")
    static JSONObject listFriend(String currentUser) {
        List<String> friends = db.getFriendships(currentUser);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(friends);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friends", jsonArray);
        return buildSuccessReply(jsonObject);
    }

    static JSONObject createRoom(String currentUser, JSONObject params) {
        if(!db.addRoom((String) params.get("name"), currentUser))
            return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    @SuppressWarnings("unchecked")
    static JSONObject addMe(JSONObject params) {
        String broadcastIp = null; //TODO: db.getBroadcastIp((String) params.get("name")); //return null se la stanza non esiste
        if(broadcastIp == null) return buildErrorReply(400, "Database error.");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcast-ip", broadcastIp);
        return buildSuccessReply(jsonObject);
    }

    @SuppressWarnings("unchecked")
    static JSONObject chatList(String currentUser) {
        List<String> rooms = db.getRooms();
        // TODO: deve dire a quali chatroom sono iscritto??
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(rooms);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rooms", jsonArray);
        return buildSuccessReply(jsonObject);
    }

    static JSONObject closeRoom(String currentUser, JSONObject params) {
        String name = (String) params.get("name");
        String creator = db.getCreator(name);
        if(creator == null) return buildErrorReply(400, "XRoom not exists.");
        if(!creator.equals(currentUser))
            return buildErrorReply(403, "Only the creator can close the room.");
        if(!db.deleteRoom(name)) return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    static JSONObject file2friend(String currentUser, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }

    static JSONObject msg2friend(String currentUser, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }

    static JSONObject chatroomMessage(String currentUser, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }
}
