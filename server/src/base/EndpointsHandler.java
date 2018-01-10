package base;

import Connections.Multicast;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.Socket;
import java.util.List;

import static base.Endpoints.FILE2FRIEND;
import static base.Endpoints.MSG2FRIEND;
import static base.RequestsHandler.buildErrorReply;
import static base.RequestsHandler.buildSuccessReply;
import static base.Utils.isDebug;
import static base.Utils.printDebug;
import static base.Utils.writeToFile;

class EndpointsHandler {

    private static final Database db = new Database();
    private static String lastBroadcastIP = null;
    private static final String LASTIP_PATH = "last-broadcast-ip.txt";
    
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
            jsonFriend.put("online", OnlineUsers.isOnline(friend));

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
            jsonRoom.put("address", db.getBroadcastIP(room));
            jsonRoom.put("creator", db.getCreator(room));
            jsonRoom.put("subscribed", subscriptions.contains(room));
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
        
        if(!db.existUser(username))
            return buildErrorReply(401, "Username not found.");
    
        if(!checkPassword(username, password))
            return buildErrorReply(401, "Incorrect password.");
        
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
        
        user.setHeartbeat(System.currentTimeMillis());
        
        if (OnlineUsers.isOnline(user)) {
            // All sockets have been correctly set up - notify his friends
            printDebug("Logged in: " + user.getUsername());
            for (User friend : OnlineUsers.getOnlineFriends(user)) {
                friend.notifyFriendStatus(user.getUsername(), true);
            }
        }
        
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
        if(user.getUsername().equals(username))
            return buildErrorReply(400, "You can't add yourself as a friend.");
        if(!db.existUser(username))
            return buildErrorReply(400, "User not exists.");
        if(db.checkFriendship(user.getUsername(), username))
            return buildErrorReply(400, "You are already friend of this user!");
        if(!db.addFriendship(user.getUsername(), username))
            return buildErrorReply(400, "A database error occured.");
        if (OnlineUsers.isOnline(username))
            OnlineUsers.getByUsername(username).notifyNewFriend(user.getUsername());
        return buildSuccessReply();
    }

    static JSONObject isOnline(User user, JSONObject params) {
        String username = (String) params.get("username");
        if (!db.existUser(username))
            return buildErrorReply(404, "Username not found.");
        if (!db.checkFriendship(user.getUsername(), username))
            return buildErrorReply(403, "You are not friend of this user.");
        
        JSONObject result = new JSONObject();
        result.put("online", OnlineUsers.isOnline(username));
        return buildSuccessReply(result);
    }

    @SuppressWarnings({"unchecked", "unused"})
    static JSONObject listFriend(User user, JSONObject params) {
        List<String> friends = db.getFriendships(user.getUsername());
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friends", getFriendsStatus(friends));
        return buildSuccessReply(jsonObject);
    }

    static JSONObject createRoom(User user, JSONObject params) {
        if (lastBroadcastIP == null) lastBroadcastIP = Utils.readFromFile(LASTIP_PATH);
        String nextBroadcastIP = Utils.nextBroadcastIP(lastBroadcastIP);
        if (nextBroadcastIP == null) {
            System.err.println("No more multicast address left!");
            return buildErrorReply(500, "Internal server error");
        }
        
        String roomName = (String) params.get("room");
        if(!db.addRoom(roomName, user.getUsername(), nextBroadcastIP) || !db.addSubscription(user.getUsername(), roomName))
            return buildErrorReply(400, "Database error.");
        
        lastBroadcastIP = nextBroadcastIP;  // only if success
        writeToFile(nextBroadcastIP, LASTIP_PATH);
        JSONObject result = new JSONObject();
        result.put("name", roomName);
        result.put("address", nextBroadcastIP);
        
        return buildSuccessReply(result);
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
        List<String> subscriptions = db.getUserSubscriptions(user.getUsername());
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
        
        String broadcastIp = db.getBroadcastIP(room);
        JSONObject closedMsg = new JSONObject();
        closedMsg.put("recipient", room);
        closedMsg.put("chat_closed", user.getUsername() + " closed this chatroom.");
        Multicast.broadcast(closedMsg.toJSONString(), broadcastIp);
        
        return buildSuccessReply();
    }

    static JSONObject file2friend(User user, JSONObject params) {
        String from = (String) params.get("from");
        String to = (String) params.get("to");
        Integer port =  Integer.parseInt((String) params.get("port"));
        String filename = (String) params.get("filename");
        
        if (
                from == null || to == null  || filename == null ||
                port <= 1024 || to.equals(user.getUsername())
                ) {
            return buildErrorReply(400, "Malformed request.");
        }
        else if (!db.existUser(to)) {
            return buildErrorReply(404, "User not found");
        }
        else if (!db.checkFriendship(user.getUsername(), to)) {
            return buildErrorReply(403, to + " is not your friend.");
        }
        else if (!OnlineUsers.isOnline(to)) {
            return buildErrorReply(449, "User is currently offline.");
        }
        
        params.put("hostname", user.getMessageSocket().getInetAddress().getHostName());
        
        OnlineUsers.getByUsername(to).sendMsgRequest(FILE2FRIEND, params);
        
        return buildSuccessReply();
    }

    static JSONObject msg2friend(User user, JSONObject params) {
        String from = user.getUsername();
        String to = (String) params.get("recipient");
        String text = (String) params.get("text");
        
        if (to == null || text == null)
            return buildErrorReply(400, "Malformed request");
        else if (from.equals(to))
            return buildErrorReply(403, "You can't send a message to yourself.");
        else if (!db.checkFriendship(from, to))
            return buildErrorReply(403, to + " is not your friend.");
        else if (text.length() == 0)
            return buildErrorReply(400, "Text cannot be empty.");
        else if(!OnlineUsers.isOnline(to))
            return buildErrorReply(400, to +" is offline.");
        
        OnlineUsers.getByUsername(to).sendMsgRequest(MSG2FRIEND, params);
        return buildSuccessReply();
    }

    static JSONObject chatroomMessage(User user, JSONObject params) {
        return buildErrorReply(400, "Not implemented yet.");
    }
}
