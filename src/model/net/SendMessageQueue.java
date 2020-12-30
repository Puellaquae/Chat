package model.net;

import model.protocol.Envelope;
import model.util.Config;

import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SendMessageQueue {
    Config config;
    ConcurrentHashMap<Envelope, Integer> envelopeLife = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<Envelope> queue = new ConcurrentLinkedQueue<>();
    Lock emptyLock = new ReentrantLock();
    Condition emptyCondition = emptyLock.newCondition();
    Thread sendThread = new Thread(() -> {
        while (true) {
            emptyLock.lock();
            try {
                emptyCondition.await(config.getWaitTime(), TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                emptyLock.unlock();
            }
            queue.removeIf(envelope -> !envelopeLife.containsKey(envelope) || envelopeLife.get(envelope) < 0);
            for (Envelope envelope : queue) {
                System.out.println("Broadcast: " + Arrays.toString(envelope.serialize()));
                UDPHelper.Broadcast(config.getPort(), envelope.serialize());
                envelopeLife.put(envelope, envelopeLife.get(envelope) - 1);
            }
        }
    });

    public SendMessageQueue(Config config) {
        this.config = config;
        sendThread.setDaemon(true);
        sendThread.start();
    }

    public void add(Envelope envelope) {
        emptyLock.lock();
        envelopeLife.put(envelope, config.getRetransmitTimes());
        queue.add(envelope);
        emptyCondition.signalAll();
        emptyLock.unlock();
    }

    public void ack(Envelope envelope) {
        envelopeLife.remove(envelope);
    }
}
