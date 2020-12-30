package model.protocol;

public class ListenMessage extends Message {
    public String id;
    public static final String TYPE_NAME = "listen";
    public ListenMessage() {
        type = TYPE_NAME;
    }
}
