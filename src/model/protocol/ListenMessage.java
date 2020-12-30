package model.protocol;

import model.util.EncodeUtils;

public class ListenMessage extends Message {
    private String id;
    public static final String TYPE_NAME = "listen";
    public ListenMessage() {
        type = TYPE_NAME;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
