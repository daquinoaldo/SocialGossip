import exceptions.*;

import java.io.File;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;

public class MainServer {

    private static SQLiteHelper sqLiteHelper = new SQLiteHelper();

    /* Auxiliar methods for login */
    private static String md5(String password) {
        try {
            return new String(MessageDigest.getInstance("MD5").digest(password.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if the given password is the same of the encrypted one.
     * @param password to be checked
     * @param md5 of the password of the user
     * @return true if password is correct, false otherwise
     */
    private static boolean checkPassword (String password, String md5) {
        try {
            return Arrays.equals(MessageDigest.getInstance("MD5").digest(password.getBytes()), md5.getBytes());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /* TUTTE LE CONNESSIONI DEVONO ESSERE TCP */
    /* È OBBLIGATORIO USARE NIO dove c'è scritto, sarebbe meglio usarlo ovunque */

    /* NICKNAME non sarebbe più corretto chiamarlo USERNAME?
    E REGISTER cambiarlo in SIGNUP? */
    
    /**
     * Register a new user. After registration user is not logged-in, need to call the login function.
     * @param nickname the username you want to register
     * @param password the chosen password
     * @param language in the ISO 639-1 standard (like "en")
     * @return true if the user is correctly registered, false if the username already exist in the database
     */
    private static void register(String nickname, String password, String language) {
        sqLiteHelper.addUser(nickname, md5(password), language);
    }
    
    /* NotRegisteredException potrebbe essere sostituita da UserNotExistException */
    /**
     * Login with an existent user.
     * @param nickname of the user that want to log in
     * @param password the password previously chosen
     * @return true if the user is authenticated successfully, false otherwise and throw an exception
     * @throws UserNotExistException if the username doesn't exist in database
     * @throws WrongPasswordException if the username is registered with another password
     */
    private static boolean login(String nickname, String password)
            throws UserNotExistException, WrongPasswordException {
        if(!sqLiteHelper.existUser(nickname)) throw new UserNotExistException();
        if(!checkPassword(password, sqLiteHelper.getPassword(nickname))) throw new WrongPasswordException();
        else {
            // TODO: cambio lo stato dopo il login
            // TODO: broadcast cambio stato
            return true;
        }
    }
    
    /**
     * Exit the account.
     * @return true if successfully logged out, false if not logged in
     */
    private static boolean logout() {
        // TODO: setto lo stato su offline
        return false;
    }
    
    /**
     * Find information about a user.
     * @param nickname of the user you want to search
     * @return the user if exist, null if not exist
     */
    private static User lookUp (String nickname) {
        return null;
        // TODO: si potrebbe usare sqLiteHelper.existUser(nickname) per vedere se esite,
        // oppure usare la ricerca parziale in sql
    }
    
    /**
     * Add a person(nickname) to your friends.
     * The other person doesn't have to accept but will notice that now you and him/her are friends.
     * @param nickname of the person you want to add to friends
     * @return true if success, false if you and nickname are already friends
     * @throws UserNotExistException if nickname is not a registered user
     */
    private static boolean friendship(String currentUser, String nickname) throws UserNotExistException {
        sqLiteHelper.addFriendship(currentUser, nickname);
        // TODO: server notifica nickname che siete amici
        return true;
    }
    
    /**
     * Get a list of all your friends.
     * @return a List of friends, empty if you don't have friends
     */
    private static List listFriend() {
        return null;
    }
    
    /**
     * Create a new chat room.
     * Note that just create the chat room, don't add you to that. To enter the chat use addme(chatId)
     * @param name the title of the chat room
     * @return true if success, false if a chat room with this name already exists
     */
    private static boolean createRoom(String currentUser, String name) {
        return sqLiteHelper.addRoom(name, currentUser);
    }
    
    /**
     * Add you in the specified chat room.
     * @param chatId the title of the chat
     * @return true if success, false if already in the chat
     * @throws ChatNotExistException if not exist a chat with this title
     */
    private static boolean addMe(String chatId) throws ChatNotExistException {
        // TODO
        return false;
    }
    
    /**
     * Returns a list of all the existent chat rooms,
     * including those to which the user is registered, specifying what they are.
     * @return the list of all the existent chat rooms specifying those to which the user is registered
     */
    private static List chatList() {
        return null;
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
    
    
    
    
    public static void main(String[] args) {
        System.out.println("bella sta roba");
    }
}
