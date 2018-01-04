package base;

import exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.util.ArrayList;
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
        if(!db.existUser(username)) buildErrorReply(401, "base.User not exist");
        if(!checkPassword((String) params.get("password"), db.getPassword(username)))
            buildErrorReply(401, "Wrong password");
        onlineUsers.put(username, new User(username));
        // TODO: broadcast cambio stato
        return buildSuccessReply();
    }
    
    static JSONObject register(JSONObject params) {
        if(!db.addUser((String) params.get("username"), Utils.md5((String) params.get("password")),
                (String) params.get("language"))) return buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    static JSONObject lookup(JSONObject params) {
        if(!db.existUser((String) params.get("username")))
            return buildErrorReply(400, "User not exist.");
        return buildSuccessReply();
    }

    static JSONObject friendship(String currentUser, JSONObject params) {
        if(!db.addFriendship(currentUser, (String) params.get("friendname")))
            buildErrorReply(400, "Database error.");
        // TODO: server notifica nickname che siete amici
        return buildSuccessReply();
    }

    static JSONObject listFriend(String currentUser, JSONObject params) {
        List<String> friends = db.getFriendships(currentUser);
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(friends);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("friends", jsonArray);
        return buildSuccessReply(jsonObject);
    }

    static JSONObject createRoom(String currentUser, JSONObject params) {
        if(!db.addRoom((String) params.get("name"), currentUser))
            buildErrorReply(400, "Database error.");
        return buildSuccessReply();
    }

    static JSONObject addMe(JSONObject params) {
        //  TODO: return error if room doesn't exist
        String broadcastIp = db.getBroadcastIp((String) params.get("name"));
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("broadcast-ip", broadcastIp);
        return buildSuccessReply(jsonObject);
    }

    /**
     * Returns a list of all the existent chat rooms,
     * including those to which the user is registered, specifying what they are.
     * @return the list of all the existent chat rooms specifying those to which the user is registered
     */
    private static List chatList() {
        return db.getRooms();
    }

    /* Facciamo che la può chiudere solo l'utente che l'ha aperta? */
    /**
     * Close a chat room. All users who belong to it will be informed.
     * @param chatName of the chat you want to close
     * @return true if success, false otherwise
     */
    private static boolean closeChat(String chatName) {
        return false;
    }

    /* Dobbiamo implementare anche le corrispettive funzioni per ricevere,
    che dovranno essere passate al server al momento del login. */

    private static boolean file2friend(String nickname, File file) {
        // il trasferimento deve essere fatto senza passare dal server, con TCP e NIO (fuck)
        // si passa dal server per
        return false;
    }

    private static boolean message2friend(String nickname, String message)
            throws UserNotExistException, NotFriendsException, UserOfflineException {
        return false;
    }

    private static boolean message2chat(String chatName, String message)
            throws ChatNotExistException, AllUSersOfflineException {
        return false;
    }
}
