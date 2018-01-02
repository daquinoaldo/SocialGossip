import java.sql.*;

public class SQLiteHelper {

    private static String DEFAULT_DB_NAME = "database.db";

    private String dbName = "database.db";

    SQLiteHelper(String dbName) {
        this.dbName = dbName;
    }

    SQLiteHelper() {
        this(DEFAULT_DB_NAME);
    }

    public Connection connect() {
        Connection connection = null;
        try {
            return DriverManager.getConnection("jdbc:sqlite:"+dbName);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public boolean execute(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            return statement.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return false;
        }
    }

    public ResultSet executeQuery(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            return statement.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    public String getString(ResultSet resultSet, String columnLabel) {
        try {
            return resultSet.getString(columnLabel);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    /* CREATE TABLE */
    public boolean init() {
        // Assumption: the primary key is the username, that is unique and immutable.
        String users = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	username TEXT PRIMARY KEY,\n"
                + "	password TEXT NOT NULL,\n"
                + "	name TEXT NOT NULL,\n"
                + "	surname TEXT NOT NULL\n"
                + ");";
        // Assumption: the primary key is the room name, that is unique and immutable.
        String rooms = "CREATE TABLE IF NOT EXISTS rooms (\n"
                + "	name TEXT PRIMARY KEY,\n"
                + "	creator TEXT NOT NULL\n"
                + ");";
        /* Important note:
         * In SQLite, a column with type INTEGER PRIMARY KEY is an alias for the ROWID.
         * The AUTOINCREMT keyword changes the automatic ROWID assignment algorithm to prevent the reuse of ROWIDs
         * over the lifetime of the database.
         * In other words, the purpose of AUTOINCREMENT is to prevent the reuse of ROWIDs from previously deleted rows.
         * The AUTOINCREMENT keyword imposes extra CPU, memory, disk space, and disk I/O overhead and should be avoided
         * if not strictly needed. It is usually not needed.
         *
         * We consider that messages could be deleted from database, so in this case is better use autoincrement
         * to avoid problems with the reuse of ROWIDs.
         * If not, the use of autoincrement could be avoided to improve performance.
         */
        String messages = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	room TEXT NOT NULL,\n"
                + "	author TEXT NOT NULL,\n"
                + "	content TEXT\n"
                + ");";
        return execute(users) && execute(rooms) && execute(messages);
        // Important note: if execute(users) fails, the othe excute will not be called
    }

    /* INSERT */
    public boolean addUser(String username, String password, String name, String surname) {
        String sql = "INSERT INTO users(username, password, name, surname) " +
                "VALUES("+username+", "+password+", "+name+", "+surname+")";
        return execute(sql);
    }

    public boolean addRoom(String name, String creator) {
        String sql = "INSERT INTO users(name, creator) " +
                "VALUES("+name+", "+creator+")";
        return execute(sql);
    }

    public boolean addMessage(String room, String author, String content) {
        String sql = "INSERT INTO users(room, author, content) " +
                "VALUES("+room+", "+author+", "+content+")";
        return execute(sql);
    }

    /* DELETE */
    /**
     * Delete the user, all his messages and all the rooms that the user have created. The username is available again.
     * @param username of the user you want to delete
     * @return true for success, false otherwise
     */
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = "+username;
        return execute(sql);
        // TODO: chiamare deleteRoom per tutte le stanze
        // TODO: chiamare deleteMessage per tutti i messaggi
    }

    /**
     * Delete the room and all the messages in the room.
     * @param name of the room you want to delete
     * @return true for success, false otherwise
     */
    public boolean deleteRoom(String name) {
        String sql = "DELETE FROM rooms WHERE name = "+name;
        return execute(sql);
        // TODO: chiamare deleteMessage per tutti i messaggi
    }

    public boolean deleteMessage(String id) {
        String sql = "DELETE FROM messages WHERE id = "+id;
        return execute(sql);
    }

    /* SELECT */
    public String getPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = "+username;
        ResultSet resultSet = executeQuery(sql);
        return getString(resultSet, "password");
    }

    public String getCreator(String name) {
        String sql = "SELECT creator FROM rooms WHERE name = "+name;
        ResultSet resultSet = executeQuery(sql);
        return getString(resultSet, "creator");
    }

    public String getMessage(int id) {
        String sql = "SELECT * FROM messages WHERE id = "+id;
        ResultSet resultSet = executeQuery(sql);
        return null;    // TODO: classe Messaggio
    }

}