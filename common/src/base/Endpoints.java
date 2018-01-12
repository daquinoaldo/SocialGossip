package base;// Register endpoints name here, for REST-like API

interface Endpoints {
    String HEARTBEAT = "HEARTBEAT";
    String LOGIN = "LOGIN";
    String REGISTER = "REGISTER";
    String LOOKUP = "LOOKUP";
    String FRIENDSHIP = "FRIENDSHIP";
    String IS_ONLINE = "IS_ONLINE";
    String LIST_FRIEND = "LIST_FRIEND";
    String CREATE_ROOM = "CREATE_ROOM";
    String ADD_ME = "ADD_ME";
    String CHAT_LIST = "CHAT_LIST";
    String CLOSE_ROOM = "CLOSE_ROOM";
    String FILE2FRIEND = "FILE2FRIEND";
    String MSG2FRIEND = "MSG2FRIEND";
    String ROOM_MESSAGE = "ROOM_MESSAGE";
}
