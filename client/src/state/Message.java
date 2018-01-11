package state;

public  class Message {
    public final String sender;
    public final String text;
    public Message(String sender, String text) {
        this.sender = sender;
        this.text = text;
    }
    public String toString() { return "<" + sender + ">: " + text; }
}
