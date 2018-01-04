package base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    
    public static boolean isDebug = System.getenv().getOrDefault("DEBUG", "false").equals("TRUE");
    
    /**
     * Return MD5 hash of a String.
     * @param str String to be hashed.
     * @return The MD5 hash as a String, or null if the computation failed.
     * @
     */
    public  static String md5(String str) {
        try {
            return new String(MessageDigest.getInstance("MD5").digest(str.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error while calculating MD5 hash of " + str);
            e.printStackTrace();
            return null;
        }
    }
    
}
