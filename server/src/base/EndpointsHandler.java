package base;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.util.List;

import static base.Endpoints.FILE2FRIEND;
import static base.Endpoints.MSG2FRIEND;
import static base.RequestsHandler.buildErrorReply;
import static base.RequestsHandler.buildSuccessReply;
import static base.Utils.isDebug;

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
    
    private static JSONObject buildRequest(String endpoint, JSONObject params) {
        JSONObject req = new JSONObject();
        req.put("endpoint", endpoint);
        req.put("params", params);
        return req;
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

    static JSONObject heartbeat(User user, JSONObject params) {
        user.setHeartbeat( System.currentTimeMillis() );
        return new JSONObject();
    }
    
    @SuppressWarnings({"unchecked", "unused"})
    static JSONObject login(User stubUser, JSONObject params) {
        Socket primarySocket = stubUser.getPrimarySocket();
        Socket messageSocket = stubUser.getMessageSocket();
        String username = (String) params.get("username");
        String password = (String) params.get("password");
    
        if (isDebug)
            System.out.println("Login request: <" + username + "," + password + ">");
        
        if(!db.existUser(username))
            return buildErrorReply(401, "Username not found.");
    
        if (isDebug)
            System.out.println("User found in database, checking password...");
    
        if(!checkPassword(username, password))
            return buildErrorReply(401, "Incorrect password.");
    
        if (isDebug)
            System.out.println("Password correct, adding to OnlineUsers");
        
        // Check if this is not first login request (-> update socket informations)
        User user = OnlineUsers.getByUsername(username);
        
        if (user == null) {
            // First login request, adding a new user to OnlineUsers
            user = new User(username);
            boolean success = OnlineUsers.add(user);
            if (!success) return buildErrorReply(503, "Error while changing user status.");
        }
        
        // Update socket informations
        if (primarySocket != null)
            user.setPrimarySocket(primarySocket);
        if (messageSocket != null)
            user.setMessageSocket(messageSocket);
        
        return buildSuccessReply();
    }

    @SuppressWarnings("unused")
    static JSONObject register(User stubUser, JSONObject params) {
        String username = (String) params.get("username");
        String password = (String) params.get("password");
        String language = (String) params.get("language");
    
        if (isDebug) System.out.println("Register request: <" + username + "," + password + "," + language + ">");
    
        if(db.existUser(username))
            return buildErrorReply(400, "User already exists.");
        if(!db.addUser(username, Utils.md5(password), language))
            return buildErrorReply(400, "Database error.");
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
        if (OnlineUsers.isOnline(username))
            OnlineUsers.getByUsername(username).notifyNewFriend(user.getUsername());
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
        String from = (String) params.get("from");
        String to = (String) params.get("to");
        int port = (int) params.get("to");
        String hostname = (String) params.get("to");
        
        if (!db.existUser(to)) {
            return buildErrorReply(404, "User not found");
        }
        else if (!db.checkFriendship(user.getUsername(), to)) {
            return buildErrorReply(403, to + " is not your friend.");
        }
        else if (!OnlineUsers.isOnline(to)) {
            return buildErrorReply(449, "User is currently offline.");
        }
        else if (port <= 1024 || hostname == null || hostname.length() == 0 || !from.equals(user.getUsername())) {
            return buildErrorReply(400, "Invalid request.");
        }
        
        JSONObject request = buildRequest(FILE2FRIEND, params);
        OnlineUsers.getByUsername(to).sendMsgRequest(request.toJSONString());
        
        return buildSuccessReply();
    }

    static JSONObject msg2friend(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }

    static JSONObject chatroomMessage(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }
}
