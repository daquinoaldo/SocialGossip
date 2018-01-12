package misc;

import org.sqlite.SQLiteConfig;

import java.sql.*;
import java.util.ArrayList;

/**
 * The database helper: the state of server has a permanent copy in the database.
 * This class contains all the methods to operate with it.
 * The database is implemented in SQLite.
 */
@SuppressWarnings("ConstantConditions")
public class Database {

    private static final String DEFAULT_DB_NAME = "database.db";

    private final String dbName;

    public Database(String dbName) {
        this.dbName = dbName;
    }

    public Database() {
        this(DEFAULT_DB_NAME);
    }

    private Connection connect() {
        try {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            return DriverManager.getConnection("jdbc:sqlite:"+dbName, config.toProperties());
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Connects to database and exec the sql query, throwing exception if there are errors
    private void execute(String sql) throws SQLException {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        }
        // IMPORTANT NOTE: the use of a try without catch clause may seem dumb,
        // but it has the utility of being able to use try with resources.
    }

    // Like execute, but return false in case of errors without throwing exception
    private boolean execBool(String sql) {
        try {
            execute(sql);
            return true;
        }
        catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Execute a query and returns the String result at first row in columnLabel
    private String getString(String sql, String columnLabel) throws SQLException {
        try (Connection connection = connect();
                Statement statement = connection.createStatement()) {
            // Execute query
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.isClosed())
                return null;
            return resultSet.getString(columnLabel);
        }
    }

    // Same of getString but return an ArrayList of String, one for any row at column columnLabel
    private ArrayList<String> getList(String sql, String columnLabel) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ArrayList<String> result = new ArrayList<>();
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next())
                result.add(resultSet.getString(columnLabel));
            return result;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Return the number of results that a COUNT(*) query produce
    private int getCount(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            return statement.executeQuery(sql).getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }

    /* CREATE TABLE */
    public void init() {
        // Assumption: the primary key is the username, that is unique and immutable.
        String users = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	username TEXT PRIMARY KEY,\n"
                + "	password TEXT NOT NULL,\n"
                + "	language TEXT NOT NULL\n"
                + ");";
        try {
            execute(users);
        } catch (SQLException e){
            System.err.println("Fatal error while creating users table");
            e.printStackTrace();
            System.exit(1);
        }
        /* Assumption: the primary key is the couple of 2 friends.
         * Important note: the table admits duplicates like
         * user1: goofy; user2: minnie;
         * user1: minnie; user2: goofy;
         * In SQLite it is not possible to put this constraint, so the check must be done at the time of insertion.
         */
        String friendships = "CREATE TABLE IF NOT EXISTS friendships (\n"
                + "	user1 TEXT NOT NULL,\n"
                + "	user2 TEXT NOT NULL,\n"
                + " PRIMARY KEY (user1, user2),\n"
                + " FOREIGN KEY (user1) REFERENCES users (username),\n"
                + " FOREIGN KEY (user2) REFERENCES users (username)\n"
                + ");";
        try {
            execute(friendships);
        } catch (SQLException e){
            System.err.println("Fatal error while creating friendships table");
            e.printStackTrace();
            System.exit(1);
        }
        // Assumption: the primary key is the room name, that is unique and immutable.
        String rooms = "CREATE TABLE IF NOT EXISTS rooms (\n"
                + "	name TEXT PRIMARY KEY,\n"
                + "	creator TEXT NOT NULL,\n"
                + "	broadcastIP TEXT NOT NULL UNIQUE,\n"
                + " FOREIGN KEY (creator) REFERENCES users(username)\n"
                + ");";
        try {
            execute(rooms);
        } catch (SQLException e){
            System.err.println("Fatal error while creating rooms table");
            e.printStackTrace();
            System.exit(1);
        }
        String subscriptions = "CREATE TABLE IF NOT EXISTS subscriptions (\n"
                + "	username TEXT NOT NULL,\n"
                + "	room TEXT NOT NULL,\n"
                + " PRIMARY KEY (username, room),\n"
                + " FOREIGN KEY (username) REFERENCES users (username),\n"
                + " FOREIGN KEY (room) REFERENCES rooms (name)\n"
                + ");";
        try {
            execute(subscriptions);
        } catch (SQLException e){
            System.err.println("Fatal error while creating subscriptions table");
            e.printStackTrace();
            System.exit(1);
        }
    }

    /* INSERT */
    public boolean addUser(String username, String password, String language) {
        String sql = "INSERT INTO users (username, password, language) " +
                "VALUES('"+username+"', '"+password+"', '"+language+"')";
        try {
            execute(sql);
            return true;
        }
        catch (SQLException e) {
            System.err.println("Can't register user: <" + username + "," + password + "," + language);
            e.printStackTrace();
            return false;
        }
    }

    /* Important note: the table admits duplicates like
     * user1: goofy; user2: minnie;
     * user1: minnie; user2: goofy;
     * The check can be done in SQL with a WHERE clause or calling the function tha check if there is already a
     * friendship. We have chosen to do it with the checkFriendship(user1, user2) function.
     */
    public boolean addFriendship(String user1, String user2) {
        if (!user1.equals(user2) && !checkFriendship(user1, user2)) {
            String sql = "INSERT INTO friendships(user1, user2) " +
                    "VALUES('"+user1+"', '"+user2+"')";
            return execBool(sql);
        } else return false;
    }

    public boolean addRoom(String name, String creator, String broadcastIP) {
        String sql = "INSERT INTO rooms(name, creator, broadcastIP) " +
                "VALUES('"+name+"', '"+creator+"', '"+broadcastIP+"')";
        return execBool(sql);
    }

    /* Important note: the table admits duplicates like
     * user1: goofy; user2: minnie;
     * user1: minnie; user2: goofy;
     * The check can be done in SQL with a WHERE clause or calling the function tha check if there is already a
     * friendship. We have chosen to do it with the checkFriendship(user1, user2) function.
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean addSubscription(String username, String room) {
        String sql = "INSERT INTO subscriptions(username, room) " +
                "VALUES('"+username+"', '"+room+"')";
        return execBool(sql);
    }

    private boolean deleteAllSubscription(String room) {
        String sql = "DELETE FROM subscriptions WHERE room = '"+room+"'";
        return execBool(sql);
    }

    public boolean deleteRoom(String room) {
        if (!deleteAllSubscription(room)) return false;
        String sql = "DELETE FROM rooms WHERE name = '"+room+"'";
        return execBool(sql);
    }

    /* SELECT */
    public boolean existUser(String username) {
        String sql = "SELECT count(*) FROM users WHERE username = '"+username+"'";
        return getCount(sql) > 0;
    }

    public String getPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = '"+username+"'";
        try {
            return getString(sql, "password");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public String getUserLang(String username) {
        String sql = "SELECT language FROM users WHERE username = '"+username+"'";
        try {
            return getString(sql, "language");
        }
        catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean checkFriendship(String user1, String user2) {
        String sql = "SELECT count(*) FROM friendships WHERE (user1 = '"+user1+"' AND user2 = '"+user2+"') OR " +
                "(user1 = '"+user2+"' AND user2 = '"+user1+"')";
        return getCount(sql) > 0;
    }

    @SuppressWarnings("unchecked")
    public ArrayList<String> getFriendships(String username) {
        String sql1 = "SELECT user2 FROM friendships WHERE user1 = '"+username+"'";
        ArrayList array1 = getList(sql1,"user2");
        String sql2 = "SELECT user1 FROM friendships WHERE user2 = '"+username+"'";
        ArrayList array2 = getList(sql2,"user1");
        if (array1 == null) return array2;
        if (array2 == null) return array1;
        array1.addAll(array2);
        return array1;
    }

    public String getCreator(String name) {
        String sql = "SELECT creator FROM rooms WHERE name = '"+name+"'";
        try {
            return getString(sql, "creator");
        }
        catch (SQLException e) {
            return null;
        }
    }

    public String getBroadcastIP(String name) {
        String sql = "SELECT broadcastIP FROM rooms WHERE name = '"+name+"'";
        try {
            return getString(sql, "broadcastIP");
        }
        catch (SQLException e) {
            return null;
        }
    }

    public ArrayList<String> getRooms() {
        String sql = "SELECT name FROM rooms";
        return getList(sql,"name");
    }

    public ArrayList<String> getUserSubscriptions(String username) {
        String sql = "SELECT room FROM subscriptions WHERE username = '"+username+"'";
        return getList(sql,"room");
    }
    
    public ArrayList<String> getRoomSubscribers(String roomName) {
        String sql = "SELECT username FROM subscriptions WHERE room = '" + roomName + "'";
        return getList(sql, "username");
    }

}