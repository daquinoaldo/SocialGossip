package State;

public  class Message {
    final String sender;
    final String text;
    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }
    public String toString() { return "<" + sender + ">: " + text; }
}
