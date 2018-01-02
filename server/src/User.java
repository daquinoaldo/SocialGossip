import exceptions.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class User {
    private String username;
    private byte[] password;
    private UserStatus userStatus;

    public User(String username, String password) throws NoSuchAlgorithmException {
        if (username == null) throw new EmptyUsernameException();
        if (password == null) throw new EmptyPasswordException();
        this.username = username;
        this.password = MessageDigest.getInstance("MD5").digest(password.getBytes());
        userStatus = UserStatus.OFFLINE;
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
}
