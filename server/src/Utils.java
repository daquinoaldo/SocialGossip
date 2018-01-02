public class Utils {
    public static boolean isDebug = System.getenv().getOrDefault("DEBUG", "false").equals("TRUE");
}
