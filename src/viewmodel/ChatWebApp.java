package viewmodel;

import io.javalin.Javalin;
import model.ChatNode;

public class ChatWebApp {
    public static void main(String[] args) {
        ChatNode chatNode = new ChatNode();
        Javalin app = Javalin.create(config -> config.addStaticFiles("view")).start(5950);
        app.ws("/chat", ws -> {
            ws.onConnect(ctx -> {
                System.out.println("Join: " + ctx.toString());
            });
            ws.onClose(ctx -> {
                System.out.println("Leave: " + ctx.toString() + ", for: " + ctx.reason());
            });
            ws.onMessage(chatNode::newMessage);
        });
        app.exception(Exception.class, (e, ctx) -> {
            System.out.println("In" + ctx.toString());
            e.printStackTrace();
        });
        app.wsException(Exception.class, (e, ctx) -> {
            System.out.println("In" + ctx.toString());
            e.printStackTrace();
        });
    }
}
