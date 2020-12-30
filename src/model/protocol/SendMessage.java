package model.protocol;

public class SendMessage extends Message {
    public static final String TYPE_NAME = "send";
    public String sender;
    public String recipient;
    public String text;

    public SendMessage() {
        type = TYPE_NAME;
    }
}
