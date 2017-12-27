package gui;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validators {
    public static boolean isValidUsername(String username) {
        Pattern p = Pattern.compile("[^a-z0-9]", Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(username);
        
        return username.length() > 0 && !m.find();
    }
    
    public static boolean isValidPassword(String password) {
        return password.length() > 0;
    }
}
