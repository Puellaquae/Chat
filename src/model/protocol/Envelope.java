package model.protocol;

import model.util.Configs;
import model.util.CryptoUtils;
import model.util.EncodeUtils;

import java.util.Arrays;
import java.util.Objects;

public class Envelope {
    short TTL;
    long timeTick;
    byte[] sender;
    byte[] recipient;
    byte[] hash;
    int dataLength;
    byte[] data;

    public static Envelope seal(SendMessage letter) {
        Envelope envelope = new Envelope();
        envelope.TTL = Configs.DEFAULT_TIME_TO_LIVE;
        envelope.timeTick = System.currentTimeMillis();
        envelope.sender = EncodeUtils.hex2Bytes(letter.getSender());
        envelope.recipient = EncodeUtils.hex2Bytes(letter.getRecipient());
        envelope.data = EncodeUtils.str2Bytes(letter.text);
        envelope.dataLength = envelope.data.length;
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
        int offset = Short.BYTES + Long.BYTES;
        envelope.sender = Arrays.copyOfRange(bytes, offset, offset + Configs.ID_BYTES);
        offset += Configs.ID_BYTES;
        envelope.recipient = Arrays.copyOfRange(bytes, offset, offset + Configs.ID_BYTES);
        offset += Configs.ID_BYTES;
        envelope.hash = Arrays.copyOfRange(bytes, offset, offset + Configs.HASH_BYTES);
        offset += Configs.HASH_BYTES;
        envelope.dataLength = (bytes[offset] << 24) +
                (bytes[offset + 1] << 16) +
                (bytes[offset + 2] << 8) +
                (bytes[offset + 3]);
        offset += Integer.BYTES;
        envelope.data = Arrays.copyOfRange(bytes, offset, offset + envelope.dataLength);
        return envelope;
    }

    public byte[] serialize() {
        byte[] bytes = new byte[Short.BYTES + Long.BYTES + Configs.ID_BYTES * 2 + 32 + Integer.BYTES + data.length];
        int offset = 0;
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
        offset += Short.BYTES + Long.BYTES;
        System.arraycopy(sender, 0, bytes, offset, Math.min(Configs.ID_BYTES, sender.length));
        offset += Configs.ID_BYTES;
        System.arraycopy(recipient, 0, bytes, offset, Math.min(Configs.ID_BYTES, recipient.length));
        offset += Configs.ID_BYTES;
        System.arraycopy(hash, 0, bytes, offset, Configs.HASH_BYTES);
        offset += Configs.HASH_BYTES;
        bytes[offset] = ((byte) ((dataLength >> 24) & 0xFF));
        bytes[offset + 1] = ((byte) ((dataLength >> 16) & 0xFF));
        bytes[offset + 2] = ((byte) ((dataLength >> 8) & 0xFF));
        bytes[offset + 3] = ((byte) ((dataLength) & 0xFF));
        offset += Integer.BYTES;
        System.arraycopy(data, 0, bytes, offset, data.length);
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
        sendMessage.setSender(EncodeUtils.bytes2HexStr(sender));
        sendMessage.setRecipient(EncodeUtils.bytes2HexStr(recipient));
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Envelope envelope = (Envelope) o;
        return timeTick == envelope.timeTick &&
                dataLength == envelope.dataLength &&
                Arrays.equals(sender, envelope.sender) &&
                Arrays.equals(recipient, envelope.recipient) &&
                Arrays.equals(hash, envelope.hash);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(timeTick, dataLength);
        result = 31 * result + Arrays.hashCode(sender);
        result = 31 * result + Arrays.hashCode(recipient);
        result = 31 * result + Arrays.hashCode(hash);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }
}
