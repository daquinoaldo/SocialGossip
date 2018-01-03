import java.sql.*;

public class SQLiteHelper {

    private static String DEFAULT_DB_NAME = "database.db";

    private String dbName = "database.db";

    public SQLiteHelper(String dbName) {
        this.dbName = dbName;
    }
    public SQLiteHelper() {
        this(DEFAULT_DB_NAME);
    }

    private Connection connect() {
        try {
            return DriverManager.getConnection("jdbc:sqlite:"+dbName);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return null;
        }
    }

    private void execute(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            statement.execute(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    }

    private String getString(String sql, String columnLabel) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            String string = resultSet.getString(columnLabel);
            return statement.executeQuery(sql).getString(columnLabel);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private int getCount(String sql) {
        try (Connection connection = connect();
             Statement statement = connection.createStatement()) {
            return statement.executeQuery(sql).getInt(1);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
            return -1;
        }
    }

    /* CREATE TABLE */
    public void init() {
        // Enable FOREIGN KEYs
        execute("PRAGMA foreign_keys = ON");
        // Assumption: the primary key is the username, that is unique and immutable.
        String users = "CREATE TABLE IF NOT EXISTS users (\n"
                + "	username TEXT PRIMARY KEY,\n"
                + "	password TEXT NOT NULL,\n"
                + "	language TEXT NOT NULL\n"
                + ");";
        execute(users);
        // Assumption: the primary key is the room name, that is unique and immutable.
        String rooms = "CREATE TABLE IF NOT EXISTS rooms (\n"
                + "	name TEXT PRIMARY KEY,\n"
                + "	creator TEXT NOT NULL,\n"
                + " FOREIGN KEY (creator) REFERENCES users(username)\n"
                + ");";
        execute(rooms);
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
        // TODO: se non eliminiamo gli utenti, allora eliminiamo autoincrement e modifichiamo il commento sopra
        String messages = "CREATE TABLE IF NOT EXISTS messages (\n"
                + "	id INTEGER PRIMARY KEY AUTOINCREMENT,\n"
                + "	room TEXT NOT NULL,\n"
                + "	author TEXT NOT NULL,\n"
                + "	content TEXT,\n"
                + " FOREIGN KEY (room) REFERENCES rooms (name),\n"
                + " FOREIGN KEY (author) REFERENCES users (username)\n"
                + ");";
        execute(messages);
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
        execute(friendships);
    }

    /* INSERT */
    public boolean addUser(String username, String password, String language) {
        String sql = "INSERT INTO users (username, password, language) " +
                "VALUES('"+username+"', '"+password+"', '"+language+"')";
        execute(sql);
        return existUser(username);
    }

    public boolean addRoom(String name, String creator) {
        execute("PRAGMA foreign_keys = ON");
        //TODO: questa cosa continua a non funzionare...
        String sql = "INSERT INTO rooms(name, creator) " +
                "VALUES('"+name+"', '"+creator+"')";
        execute(sql);
        return getCreator(name).equals(creator);
    }

    public void addMessage(String room, String author, String content) {
        String sql = "INSERT INTO messages(room, author, content) " +
                "VALUES('"+room+"', '"+author+"', '"+content+"')";
        execute(sql);
    }

    /* Important note: the table admits duplicates like
     * user1: goofy; user2: minnie;
     * user1: minnie; user2: goofy;
     * The check can be done in SQL with a WHERE clause
     * or calling the function tha check if there is already a friendship.
     */
    public boolean addFriendship(String user1, String user2) {
        if (!user1.equals(user2) && !checkFriendship(user1, user2)) {
            String sql = "INSERT INTO friendships(user1, user2) " +
                    "VALUES('" + user1 + "', '" + user2 + "')";
            execute(sql);
            return checkFriendship(user1, user2);
        } else return false;
    }

    /* DELETE
    Per semplicità non si elimina niente.

    /**
     * Delete the user, all his messages and all the rooms that the user have created. The username is available again.
     * @param username of the user you want to delete
     * @return true for success, false otherwise
     *
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = '"+username+"'";
        return execute(sql);
        // TODO: chiamare deleteRoom per tutte le stanze
        // TODO: chiamare deleteMessage per tutti i messaggi
    }

    /**
     * Delete the room and all the messages in the room.
     * @param name of the room you want to delete
     * @return true for success, false otherwise
     *
    public boolean deleteRoom(String name) {
        String sql = "DELETE FROM rooms WHERE name = '"+name+"'";
        return execute(sql);
        // TODO: chiamare deleteMessage per tutti i messaggi
    }

    public boolean deleteMessage(String id) {
        String sql = "DELETE FROM messages WHERE id = '"+id+"'";
        return execute(sql);
    } */

    /* SELECT */
    public boolean existUser(String username) {
        String sql = "SELECT count(*) FROM users WHERE username = '"+username+"'";
        return getCount(sql) > 0;
    }

    public String getPassword(String username) {
        String sql = "SELECT password FROM users WHERE username = '"+username+"'";
        return existUser(username) ? getString(sql, "password") : null;
    }

    public String getCreator(String name) {
        String sql = "SELECT creator FROM rooms WHERE name = '"+name+"'";
        return getString(sql, "creator");
    }

    public String getMessage(int id) {
        String sql = "SELECT * FROM messages WHERE id = '"+id+"'";
        return null;    // TODO: classe Messaggio
    }

    public boolean checkFriendship(String user1, String user2) {
        String sql = "SELECT count(*) FROM friendships WHERE (user1 = '"+user1+"' AND user2 = '"+user2+"') OR " +
                "(user1 = '"+user2+"' AND user2 = '"+user1+"')";
        return getCount(sql) > 0;
    }

}