package model;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import io.javalin.websocket.WsMessageContext;
import model.net.UMB;
import model.protocol.ListenMessage;
import model.protocol.Message;
import model.protocol.SendMessage;
import model.util.Configs;

import java.util.HashMap;
import java.util.Vector;

public class ChatNode {
    HashMap<String, Vector<WsContext>> Listen = new HashMap<>();
    UMB umb = new UMB(Configs.DEFAULT_CONFIG);
    Gson gson = new Gson();

    public ChatNode() {
        umb.onReceived(this::distribute);
    }

    public void newMessage(WsMessageContext ctx) {
        Message message = Message.parse(ctx.message());
        System.out.println(message.type);
        if (message instanceof ListenMessage) {
            System.out.println("Type: " + ((ListenMessage) message).id);
            addListen(ctx, ((ListenMessage) message).id);
        } else if (message instanceof SendMessage) {
            System.out.println("Recipient: " + ((SendMessage) message).recipient);
            System.out.println("Letter: " + ((SendMessage) message).text);
            umb.send((SendMessage) message);
        }
    }

    void addListen(WsContext ctx, String id) {
        Listen.putIfAbsent(id, new Vector<>());
        Listen.get(id).add(ctx);
        umb.subscribe(id);
    }

    void removeListen(WsContext ctx, String id) {
        Listen.get(id).remove(ctx);
        if (Listen.get(id).isEmpty()) {
            Listen.remove(id);
            umb.unsubscribe(id);
        }
    }

    void distribute(SendMessage message) {
        if (Listen.containsKey(message.recipient)) {
            for (WsContext ctx : Listen.get(message.recipient)) {
                if (ctx.session.isOpen()) {
                    ctx.send(gson.toJson(message));
                } else {
                    removeListen(ctx, message.recipient);
                }
            }
        }
    }
}
