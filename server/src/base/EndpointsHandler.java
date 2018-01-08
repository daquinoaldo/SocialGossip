package base;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.rmi.RemoteException;
import java.util.List;

import static base.RequestsHandler.buildErrorReply;
import static base.RequestsHandler.buildSuccessReply;

class EndpointsHandler {

    private static final Database db = new Database();
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
            if (OnlineUsers.isOnline(friend)) jsonFriend.put("online", true);
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
    @SuppressWarnings({"unchecked", "unused"})
    static JSONObject login(User user, JSONObject params) {
        String username = (String) params.get("username");
        if(!db.existUser(username)) return buildErrorReply(401, "Username not found.");
        if(!checkPassword((String) params.get("password"), db.getPassword(username)))
            return buildErrorReply(401, "Incorrect password.");
        if(!OnlineUsers.add(new User(username)))
            return buildErrorReply(503, "Error while changing user status.");
        
        List<String> friends = db.getFriendships(username);
        for (String friend : friends)
            if (OnlineUsers.isOnline(friend)) {
                try {
                    OnlineUsers.getByUsername(friend).notify.changedStatus(username, true);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    return buildErrorReply(503, "Error while notifing user status changing.");
                }
            }
        
        JSONObject payload = new JSONObject();
        payload.put("friends", getFriendsStatus(friends));
        List<String> rooms = db.getRooms();
        List<String> subscriptions = db.getSubscriptions(username);
        payload.put("rooms", getRoomSubscriptions(rooms, subscriptions));
        return buildSuccessReply(payload);
    }

    @SuppressWarnings("unused")
    static JSONObject register(User user, JSONObject params) {
        String username = (String) params.get("username");
        if(db.existUser(username)) return buildErrorReply(400, "User already exists.");
        if(!db.addUser(username, Utils.md5((String) params.get("password")),
                (String) params.get("language"))) return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    @SuppressWarnings("unused")
    static JSONObject lookup(User user, JSONObject params) {
        if(!db.existUser((String) params.get("username")))
            return buildErrorReply(400, "User not exists.");
        return buildSuccessReply();
    }

    static JSONObject friendship(User user, JSONObject params) {
        String username = (String) params.get("username");
        if(!db.existUser(username)) return buildErrorReply(400, "User not exists.");
        if(!db.addFriendship(user.getUsername(), username))
            return buildErrorReply(400, "Database error.");
        if (OnlineUsers.isOnline(username)) {
            try {
                OnlineUsers.getByUsername(username).notify.newFriend(user.getUsername());
            } catch (RemoteException e) {
                e.printStackTrace();
                return buildErrorReply(503, "Error while notifing user status changing.");
            }
        }
        return buildSuccessReply();
    }

    @SuppressWarnings({"unchecked", "unused"})
    static JSONObject listFriend(User user, JSONObject params) {
        List<String> friends = db.getFriendships(user.getUsername());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friends", getFriendsStatus(friends));
        return buildSuccessReply(jsonObject);
    }

    static JSONObject createRoom(User user, JSONObject params) {
        String nextBroadcastIP = Utils.nextBroadcastIP(lastBroadcastIP);
        if(!db.addRoom((String) params.get("room"), user.getUsername(), nextBroadcastIP))
            return buildErrorReply(400, "Database error.");
        lastBroadcastIP = nextBroadcastIP;  // only if success
        return buildSuccessReply();
    }

    @SuppressWarnings("unchecked")
    static JSONObject addMe(User user, JSONObject params) {
        String room = (String) params.get("room");
        String broadcastIp = db.getBroadcastIP(room);
        if(broadcastIp == null) return buildErrorReply(400, "Database error.");
        if(!db.addSubscription(user.getUsername(), room)) buildErrorReply(400, "Database error.");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcastIP", broadcastIp);
        return buildSuccessReply(jsonObject);
    }

    @SuppressWarnings({"unchecked", "unused"})
    static JSONObject chatList(User user, JSONObject params) {
        List<String> rooms = db.getRooms();
        List<String> subscriptions = db.getSubscriptions(user.getUsername());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rooms", getRoomSubscriptions(rooms, subscriptions));
        return buildSuccessReply(jsonObject);
    }

    static JSONObject closeRoom(User user, JSONObject params) {
        String room = (String) params.get("room");
        String creator = db.getCreator(room);
        if(creator == null) return buildErrorReply(400, "Room not exists.");
        if(!creator.equals(user.getUsername()))
            return buildErrorReply(403, "Only the creator can close the room.");
        if(!db.deleteRoom(room)) return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    static JSONObject file2friend(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }

    static JSONObject msg2friend(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }

    static JSONObject chatroomMessage(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }
}
