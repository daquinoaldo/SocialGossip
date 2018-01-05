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
    private static String lastBroadcastIP = "224.0.0.0";    //TODO: deve essere salvato (su file?)
    
    static {
        db.init();
    }

    /* Helpers */
    private static boolean checkPassword(String username, String password) {
        String realPasswordHash = db.getPassword(username);
        String actualPasswordHash = Utils.md5(password);
        return realPasswordHash.equals(actualPasswordHash);
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getFriendsStatus(List<String> friends) {
        JSONArray friendsWithStatus = new JSONArray();
        for (String friend : friends) {
            JSONObject jsonFriend = new JSONObject();
            jsonFriend.put("username", friend);
            if (onlineUsers.containsKey(friend)) jsonFriend.put("online", true);
            else jsonFriend.put("online", false);
            friendsWithStatus.add(jsonFriend);
        }
        return friendsWithStatus;
    }

    @SuppressWarnings("unchecked")
    private static JSONArray getRoomSubscriptions(List<String> rooms, List<String> subscriptions) {
        JSONArray roomSubscriptions = new JSONArray();
        for (String room : rooms) {
            JSONObject jsonRoom = new JSONObject();
            jsonRoom.put("name", room);
            if (subscriptions.contains(room)) jsonRoom.put("added", true);
            else jsonRoom.put("added", false);
            roomSubscriptions.add(jsonRoom);
        }
        return roomSubscriptions;
    }

    /* NOTE: In this function the friends list (friends) is scanned 2 times: one in the for loop and one in the
     * getFriendsStatus function. It was possible to avoid this doing both the notification of status change and the
     * status list in only one for loop, but this would have led to a duplication of the code because the
     * getFriendsStatus function is also used in listfriend.
     * Note that the database query is performed only one time for better performance.
     */
    @SuppressWarnings("unchecked")
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
        JSONObject payload = new JSONObject();
        payload.put("friends", getFriendsStatus(friends));
        List<String> rooms = db.getRooms();
        List<String> subscriptions = db.getSubscriptions(username);
        payload.put("rooms", getRoomSubscriptions(rooms, subscriptions));
        return buildSuccessReply(payload);
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
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friends", getFriendsStatus(friends));
        return buildSuccessReply(jsonObject);
    }

    static JSONObject createRoom(String currentUser, JSONObject params) {
        String nextBroadcastIP = Utils.nextBroadcastIP(lastBroadcastIP);
        if(!db.addRoom((String) params.get("name"), currentUser, nextBroadcastIP))
            return buildErrorReply(400, "Database error.");
        lastBroadcastIP = nextBroadcastIP;  // only if success
        return buildSuccessReply();
    }

    @SuppressWarnings("unchecked")
    static JSONObject addMe(String currentUser, JSONObject params) {
        String room = (String) params.get("room");
        String broadcastIp = db.getBroadcastIP(room);
        if(broadcastIp == null) return buildErrorReply(400, "Database error.");
        if(!db.addSubscription(currentUser, room)) buildErrorReply(400, "Database error.");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcastIP", broadcastIp);
        return buildSuccessReply(jsonObject);
    }

    @SuppressWarnings("unchecked")
    static JSONObject chatList(String currentUser) {
        List<String> rooms = db.getRooms();
        List<String> subscriptions = db.getSubscriptions(currentUser);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rooms", getRoomSubscriptions(rooms, subscriptions));
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
