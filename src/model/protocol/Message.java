package model.protocol;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Message {
    static Gson gson = new GsonBuilder().registerTypeAdapter(
            Message.class,
            new MessageDeserializer("type")
                    .registerMessageType("listen", ListenMessage.class)
                    .registerMessageType("send", SendMessage.class)
    ).create();
    public String type;
    public static Message parse(String message) {
        return gson.fromJson(message, Message.class);
    }
}
