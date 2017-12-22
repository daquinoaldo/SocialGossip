import exceptions.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User {
    private String username;
    private byte[] password;
    private UserStatus userStatus;
    private String name;
    private String surname;

    /* Ammettiamo la possibilità che ci si possa registrare anche senza specificare subito nome e cognome? */
    public User(String username, String password) throws NoSuchAlgorithmException, EmptyPasswordException {
        this(username, password, null, null);
    }

    public User(String username, String password, String name, String surname) throws NoSuchAlgorithmException {
        if (username == null) throw new EmptyUsernameException();
        if (password == null) throw new EmptyPasswordException();
        this.username = username;
        this.password = MessageDigest.getInstance("MD5").digest(password.getBytes());
        userStatus = UserStatus.OFFLINE;
        this.name = name;
        this.surname = surname;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String password) throws NoSuchAlgorithmException {
        return Arrays.equals(this.password, MessageDigest.getInstance("MD5").digest(password.getBytes()));
    }

    public UserStatus getUserStatus() {
        return userStatus;
    }
    public void setUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getFullName() {
        return name+" "+surname;
    }
}
