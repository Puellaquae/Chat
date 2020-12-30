package model.protocol;

import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

class MessageDeserializer implements JsonDeserializer<Message> {
    Gson gson;
    Map<String, Type> messageTypeRegistry;
    String messageTypeElementName;

    MessageDeserializer(String messageTypeElementName) {
        this.messageTypeElementName = messageTypeElementName;
        gson = new Gson();
        messageTypeRegistry = new HashMap<>();
    }

    MessageDeserializer registerMessageType(String typeName, Type type) {
        messageTypeRegistry.put(typeName, type);
        return this;
    }

    @Override
    public Message deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject messageObject = json.getAsJsonObject();
        JsonElement typeElement = messageObject.get(messageTypeElementName);
        return gson.fromJson(messageObject, messageTypeRegistry.get(typeElement.getAsString()));
    }
}
