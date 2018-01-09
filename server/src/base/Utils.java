package base;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Utils {
    public static final boolean isDebug = System.getenv().getOrDefault("DEBUG", "false").equals("TRUE");
    
    public static void printDebug(String s) {
        if (isDebug) System.out.println(s);
    }
    
    /**
     * Return MD5 hash of a String.
     * @param str String to be hashed.
     * @return The MD5 hash as a String, or null if the computation failed.
     * @
     */
    public static String md5(String str) {
        try {
            byte[] digest = MessageDigest.getInstance("MD5").digest(str.getBytes());
    
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < digest.length; i++) {
                String hex=Integer.toHexString(0xFF & digest[i]);
                if(hex.length()==1)
                    hexString.append('0');
        
                hexString.append(hex);
            }
            
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error while calculating MD5 hash of " + str);
            e.printStackTrace();
            return null;
        }
    }

    private static String nextIP(String actualIP) {
        String[] tokens = actualIP.split("\\.");
        if (tokens.length != 4) throw new IllegalArgumentException();
        // Starting from the last
        for (int i = 4; i >= 0; i--) {
            int item = Integer.parseInt(tokens[i]);
            // if can be incremented:
            if (item < 255) {
                tokens[i] = String.valueOf(item + 1);
                break;  // we have done
            } else { // else is 255 and then
                // set to 0 and go ahead to the i-1 token
                tokens[i] = String.valueOf(0);
            }
        }
        return String.join(".", tokens);
        // NOTE: nextIP("255.255.255.255") == "0.0.0.0"
    }

    public static String nextBroadcastIP(String actualIP) {
        String nextIP = nextIP(actualIP);
        String[] tokens = nextIP.split("\\.");
        int item = Integer.parseInt(tokens[0]);
        if (item < 224 || item > 239) return null;
        return nextIP;
    }

}
