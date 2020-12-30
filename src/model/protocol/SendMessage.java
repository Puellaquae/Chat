package model.protocol;

import model.util.EncodeUtils;

public class SendMessage extends Message {
    public static final String TYPE_NAME = "send";
    private String sender;
    private String recipient;
    public String text;

    public SendMessage() {
        type = TYPE_NAME;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
}
