package model.net;

import model.protocol.Envelope;
import model.protocol.SendMessage;
import model.util.Config;
import model.util.EncodeUtils;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

public class UMB {
    Consumer<SendMessage> onReceived;
    UDPHelper udp = new UDPHelper();
    Set<String> subscribers = Collections.newSetFromMap(new ConcurrentHashMap<>());
    Set<Envelope> distributed = Collections.newSetFromMap(new ConcurrentHashMap<>());
    Config config;
    SendMessageQueue sendMQ;

    public UMB(Config config) {
        this.config = config;
        sendMQ = new SendMessageQueue(config);
        udp.listen(config.getPort(), this::procEnvelope);
    }

    public void subscribe(String id) {
        subscribers.add(id);
    }

    public void onReceived(Consumer<SendMessage> event) {
        onReceived = event;
    }

    public void procEnvelope(byte[] rawMessage) {
        Envelope envelope = Envelope.deserialize(rawMessage);
        if (envelope.check()) {
            if (envelope.getTTL() == config.getTTL() - 1) {
                sendMQ.ack(envelope);
            }
            if (!distributed.contains(envelope) && subscribers.contains(EncodeUtils.bytes2HexStr(envelope.getRecipient()))) {
                onReceived.accept(envelope.open());
                distributed.add(envelope);
            }
            forward(envelope);
        }
    }

    void forward(Envelope envelope) {
        envelope.decTTL();
        if (envelope.getTTL() > 0) {
            send(envelope);
        }
    }

    void send(Envelope envelope) {
        sendMQ.add(envelope);
    }

    public void send(SendMessage message) {
        send(Envelope.seal(message));
    }

    public void unsubscribe(String id) {
        subscribers.remove(id);
    }
}
