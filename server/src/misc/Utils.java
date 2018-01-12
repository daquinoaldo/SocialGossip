package misc;

import java.io.*;
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
    
            StringBuilder hexString = new StringBuilder();
            for (byte digestByte : digest) {
                String hex = Integer.toHexString(0xFF & digestByte);
                if (hex.length() == 1)
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
        for (int i = 3; i >= 0; i--) {
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

    /**
     * Given a broadcast IP, returns the next IP to be used
     * @param actualIP in the quad-dotted decimal notation (239.x.x.x)
     * @return the next IP in the quad-dotted decimal notation (239.x.x.x)
     *         or null if the addresses available are finished
     */
    public static String nextBroadcastIP(String actualIP) {
        String nextIP = nextIP(actualIP);
        String[] tokens = nextIP.split("\\.");
        int item = Integer.parseInt(tokens[0]);
        if (item != 239) return null;
        return nextIP;
    }

    /**
     * Write a String in a file, used to save permanently the lastUsed
     * @param text of the string
     * @param filename path of the file
     * @return false in case of errors, true otherwise
     */
    @SuppressWarnings("SameParameterValue")
    public static boolean writeToFile(String text, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            bw.write(text+"\n");
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Read a String from a file, used to read the permanently saved copy of lastUsed
     * @param filename path of the file
     * @return the read String or 239.0.0.0 in case of error
     *         (the file not exist because is the first time the server start)
     */
    public static String readFromFile(String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            return br.readLine();
        } catch (IOException e) {
            return "239.0.0.0";
        }
    }

}
