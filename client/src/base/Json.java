package base;

import connections.Connection;
import connections.Multicast;
import state.Friend;
import state.User;
import state.Message;
import state.Room;
import gui.Utils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.net.UnknownHostException;
import java.nio.channels.ServerSocketChannel;
import java.util.ArrayList;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static base.Endpoints.*;

// TODO: se due thread inviano una richiesta insieme potrebbero incrociarsi e scambiarsi le risposte?
// mettere makeRequest synchronized risolve?

@SuppressWarnings("unchecked")
public class Json {
    private static JSONObject parse(String s) {
        if (s == null) return null;
        
        try {
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(s);
        }
        catch (ParseException e) {
            System.err.println("Got an invalid JSON: " + s);
            e.printStackTrace();
            return null;
        }
    }
    
    public static void parseMessageRequest(String jsonString) {
        JSONObject request = parse(jsonString);
        if (request == null) return;
        
        JSONObject payload = (JSONObject) request.get("params");
        String endpoint = (String) request.get("endpoint");
        
        if (endpoint == null) {
            // not a request
            String status = (String) request.get("status");
            String message = (String) request.get("message");
            if (status != null && !status.equals("ok") && message != null)
                Utils.showErrorDialog(message);
            
            return;
        }
        
        switch (endpoint) {
            case MSG2FRIEND:
                // Got a message from friend
                String sender = (String) payload.get("sender");
                String text = (String) payload.get("text");
                Message msg = new Message(sender, text);
                User.getFriend(sender).newMessage(msg);
                break;
                
            case FILE2FRIEND:
                // Got a request to send a file to me
                String filename = (String) payload.get("filename");
                String fromUsername = (String) payload.get("from");
                String hostname = (String) payload.get("hostname");
                Integer port = Integer.parseInt((String) payload.get("port"));
    
                if (filename == null || fromUsername == null || hostname == null || port < 1024) {
                    System.err.println("Invalid File2Friend request");
                    System.err.println(fromUsername + "@" + hostname + ":" + port + " - " + filename);
                    return; // malformed request
                }
                
                boolean confirm = Utils.showConfirmationDialog(fromUsername + " wants to send you a file. Save it?");
                if (!confirm) break;
                
                // aprire dialog di selezione destinazione
                File destFile = Utils.saveFileDialog(filename);
                if (destFile == null) {
                    User.getFriend(fromUsername).newMessage(
                            new Message("SYSTEM", "Download from " + fromUsername + " aborted."));
                    break;
                }
    
                User.getFriend(fromUsername).newMessage(
                        new Message("SYSTEM", "Starting download from "+fromUsername+"."));
                Connection.receiveFile(destFile, hostname, port);
                User.getFriend(fromUsername).newMessage(
                        new Message("SYSTEM", "Download from " + fromUsername + " completed.") );
                break;
                
            case CHATROOM_MESSAGE:
                // If a chatroom message is here, it's probably an error
                String status = (String) payload.get("status");
                String message = (String) payload.get("message");
                if (status != null && status.equals("err")) {
                    String roomName = (String) payload.get("recipient");
                    Room room = User.getRoom(roomName);
                    if (room != null) room.newMessage(new Message("SYSTEM", message));
                    else Utils.showErrorDialog("Chatroom error: " + message);
                }
                else System.err.println("Invalid chat message request received.");
                break;
        }
    }
    
    public static void parseChatMessage(String data) {
        JSONObject request = parse(data);
    
        String chatname = (String) request.get("recipient");
        String chatClosed = (String) request.get("chat_closed");
        if (chatClosed != null && chatClosed.length() > 0) {
            // Chatroom has been closed
            if (chatname == null) return;
            Utils.showErrorDialog(chatname + " has been closed.");
            Room room = User.getRoom(chatname);
            if (room == null) return;
            room.leaveMulticastGroup();
            room.closeWindow();
            User.removeRoom(chatname);
            return;
        }
        
        String sender = (String) request.get("sender");
        String text = (String) request.get("text");
        
        if (chatname == null || sender == null || text == null || text.length() == 0)
            return; // Malformed request
        
        if (sender.equals(User.username()))
            return; // my message
        
        Room room = User.getRoom(chatname);
        if (room == null) {
            System.err.println("Got a message for a non-subscribed room: " + chatname);
            return;
        }
        
        room.newMessage( new Message(sender, text) );
    }
    
    /* Request builders */
    public static void heartbeat() {
        makeMsgRequest(HEARTBEAT, null);
    }
    
    @SuppressWarnings("unchecked")
    public static void login(String username, String password) {
        if (username == null || password == null || username.length() == 0 || password.length() == 0)
            throw new IllegalArgumentException("Username and password must be a non-empty string.");

        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        
        JSONObject reply = makeRequest(LOGIN, parameters);
        if (reply == null)
            return;
        
        makeMsgRequest(LOGIN, parameters);
        
        User.setLoggedIn(true);
        User.setUsername(username);

        listFriends();
        chatList();
    }
    
    public static boolean register(String username, String password, String language) {
        if (username == null || password == null || language == null ||
                username.length() == 0 || password.length() == 0 || language.length() == 0)
            throw new IllegalArgumentException("Username, password and language must be a non-empty string.");
    
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        parameters.put("password", password);
        parameters.put("language", language);

        JSONObject result = makeRequest(Endpoints.REGISTER, parameters);
        return result != null;
    }
    
    public static boolean lookup(String username) {
        if (username == null || username.length() == 0)
            throw new IllegalArgumentException("Username must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        JSONObject result = makeRequest(Endpoints.LOOKUP, parameters);
        return result != null;
    }
    
    public static boolean friendship(String username) {
        if (username == null || username.length() == 0)
            throw new IllegalArgumentException("Username must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        JSONObject result = makeRequest(Endpoints.FRIENDSHIP, parameters);
        return result != null;
    }

    public static boolean isOnline(String username) {
        if (username == null || username.length() == 0)
            throw new IllegalArgumentException("Username must be a non-empty string.");
        
        Map<String, String> parameters = new HashMap<>();
        parameters.put("username", username);
        
        JSONObject result = makeRequest(Endpoints.IS_ONLINE, parameters);
        return result != null && (boolean) result.get("online");
    }
    
    private static void listFriends() {
        JSONObject result = makeRequest(Endpoints.LIST_FRIEND, null);
        if(result == null) return;
        JSONArray jsonArray = (JSONArray) result.get("friends");
        if (jsonArray == null) return; // no friends yet
        HashMap<String, Friend> friends = new HashMap<>();
        for (Object jsonObject : jsonArray) {
            String username = (String) ((JSONObject) jsonObject).get("username");
            boolean online = (boolean) ((JSONObject) jsonObject).get("online");
            friends.put(username, new Friend(username, online));
        }
        User.setFriendList(friends);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public static boolean createRoom(String roomName) {
        if (roomName == null || roomName.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", roomName);
        JSONObject result = makeRequest(Endpoints.CREATE_ROOM, parameters);
        if (result == null) return false;
        
        String address = (String) result.get("address");
        User.addRoom(roomName, address, User.username(), true);
        return true;
    }

    public static boolean addMe(String room) {
        if (room == null || room.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", room);
        JSONObject result = makeRequest(Endpoints.ADD_ME, parameters);
        return result != null;
    }

    public static void chatList() {
        JSONObject result = makeRequest(Endpoints.CHAT_LIST, null);
        if(result == null) return;
        JSONArray jsonArray = (JSONArray) result.get("rooms");
        if (jsonArray == null) return; // no chats yet
        HashMap<String, Room> rooms = new HashMap<>();
        for (Object jsonObject : jsonArray) {
            String name = (String) ((JSONObject) jsonObject).get("name");
            String address = (String) ((JSONObject) jsonObject).get("address");
            String creator = (String) ((JSONObject) jsonObject).get("creator");
            boolean subscribed = (boolean) ((JSONObject) jsonObject).get("subscribed");
            try {
                rooms.put(name, new Room(name, address, creator, subscribed));
            }
            catch (UnknownHostException e) {
                System.err.println("Unable to join the room " + name);
                e.printStackTrace();
            }
        }
        User.updateRoomList(rooms);
    }

    public static void closeRoom(String roomName) {
        if (roomName == null || roomName.length() == 0)
            throw new IllegalArgumentException("The room name must be a non-empty string.");
        Map<String, String> parameters = new HashMap<>();
        parameters.put("room", roomName);
        JSONObject result = makeRequest(Endpoints.CLOSE_ROOM, parameters);
        if(result == null) System.err.println("Json: Error in closing room "+roomName+".");
    }

    private static JSONObject genericMsg(String recipient, String text) {
        JSONObject req = new JSONObject();
        req.put("sender", User.username());
        req.put("recipient", recipient);
        req.put("text", text);
        return req;
    }
    
    public static void sendMsg(String to, String recipient) {
        JSONObject req = genericMsg(to, recipient);
        makeMsgRequest(Endpoints.MSG2FRIEND, req);
    }
    
    public static void sendChatMsg(String to, String recipient) {
        JSONObject req = genericMsg(to, recipient);
        Multicast.send(req.toJSONString());
    }
    
    public static void sendFileRequest(String toUsername) {
        File file = Utils.openFileDialog();
        if (file == null) return;
        
        JSONObject payload = new JSONObject();
        ServerSocketChannel serverSocketChannel = Connection.openFileSocket();
        
        if (serverSocketChannel == null) {
            Utils.showErrorDialog("Error while opening server socket.");
            return;
        }
    
        payload.put("filename", file.getName());
        payload.put("from", User.username());
        payload.put("to", toUsername);
        payload.put("port", Integer.toString(serverSocketChannel.socket().getLocalPort()));
    
        JSONObject result = makeRequest(FILE2FRIEND, payload);
    
        if (result != null) {
            User.getFriend(toUsername).newMessage( new Message("SYSTEM", "Starting upload to "+toUsername+".") );
            Connection.startFileSender(serverSocketChannel, file, () -> User.getFriend(toUsername).newMessage(
                    new Message("SYSTEM", "Completed upload to "+toUsername+".")));
        }
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
    private synchronized static JSONObject makeRequest(String endpoint, Map keyvalues) throws IllegalArgumentException {
        return makeGenericRequest(endpoint, keyvalues, false);
    }

    private synchronized static void makeMsgRequest(String endpoint, Map keyvalues) throws IllegalArgumentException {
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


        // send request to the server using the right socket
        if (isMsgRequest) {
            Connection.sendMsgRequest(request.toJSONString());
            return null;
        }
        
        String responseString = Connection.sendRequest(request.toJSONString());
        
        if (responseString == null) {
            Utils.showErrorDialog("Can't reach server. Check internet connection and try again.");
            return null;
        }
    
        JSONObject response = parse(responseString);
        if (isReplyOk(response)) {
            JSONObject result = (JSONObject) response.get("result");
            if (result == null) result = new JSONObject();
            return result;
        }
        else {
            String msg = (String) response.get("message");
            Utils.showErrorDialog(msg != null && msg.length() > 0 ? msg : "Unknown error.");
            return null;
        }
    }
}
