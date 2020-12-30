package model.protocol;

import model.util.Configs;
import model.util.CryptoUtils;
import model.util.EncodeUtils;

import java.util.Arrays;

public class Envelope {
    short TTL;
    long timeTick;
    byte[] sender;
    byte[] recipient;
    byte[] hash;
    byte[] data;

    public static Envelope seal(SendMessage letter) {
        Envelope envelope = new Envelope();
        envelope.TTL = Configs.DEFAULT_TIME_TO_LIVE;
        envelope.timeTick = System.currentTimeMillis();
        envelope.sender = EncodeUtils.hex2Bytes(letter.sender);
        envelope.recipient = EncodeUtils.hex2Bytes(letter.recipient);
        envelope.data = EncodeUtils.str2Bytes(letter.text);
        envelope.hash = CryptoUtils.getSHA256(envelope.data);
        return envelope;
    }

    public static Envelope deserialize(byte[] bytes) {
        Envelope envelope = new Envelope();
        envelope.TTL = (short) (bytes[0] << 8 + bytes[1]);
        envelope.timeTick = ((long) bytes[2] << 56) +
                ((long) bytes[3] << 48) +
                ((long) bytes[4] << 40) +
                ((long) bytes[5] << 32) +
                (bytes[6] << 24) +
                (bytes[7] << 16) +
                (bytes[8] << 8) +
                (bytes[9]);
        envelope.sender = Arrays.copyOfRange(bytes, 10, 138);
        envelope.recipient = Arrays.copyOfRange(bytes, 138, 138 + 128);
        envelope.hash = Arrays.copyOfRange(bytes, 138 + 128, 138 + 128 + 32);
        envelope.data = Arrays.copyOfRange(bytes, 138 + 128 + 32, bytes.length);
        return envelope;
    }

    public byte[] serialize() {
        byte[] bytes = new byte[2 + 8 + 128 + 128 + 32 + data.length];
        bytes[0] = ((byte) (TTL >> 8));
        bytes[1] = ((byte) (TTL & 0xFF));
        bytes[2] = ((byte) ((timeTick >> 56) & 0xFF));
        bytes[3] = ((byte) ((timeTick >> 48) & 0xFF));
        bytes[4] = ((byte) ((timeTick >> 40) & 0xFF));
        bytes[5] = ((byte) ((timeTick >> 32) & 0xFF));
        bytes[6] = ((byte) ((timeTick >> 24) & 0xFF));
        bytes[7] = ((byte) ((timeTick >> 16) & 0xFF));
        bytes[8] = ((byte) ((timeTick >> 8) & 0xFF));
        bytes[9] = ((byte) ((timeTick) & 0xFF));
        
        return bytes;
    }

    public byte[] getSender() {
        return sender;
    }

    public byte[] getRecipient() {
        return recipient;
    }

    public SendMessage open() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.sender = EncodeUtils.bytes2HexStr(sender);
        sendMessage.recipient = EncodeUtils.bytes2HexStr(recipient);
        sendMessage.text = EncodeUtils.bytes2Str(data);
        return sendMessage;
    }

    public short getTTL() {
        return TTL;
    }

    public void decTTL() {
        TTL--;
    }

    public boolean check() {
        return Arrays.equals(hash, CryptoUtils.getSHA256(data));
    }
}
