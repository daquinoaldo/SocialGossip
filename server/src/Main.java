public class Main {
    public static void main(String[] args) {
        Thread primary_connection = new Thread(new ConnectionHandler());
        primary_connection.start();
    }
}
