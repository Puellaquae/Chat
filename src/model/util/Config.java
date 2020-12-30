package model.util;

public class Config {
    private int port;
    private int TTL;
    private int retransmitTimes;
    private int waitTime;

    public int getWaitTime() {
        return waitTime;
    }

    public Config setWaitTime(int waitTime) {
        this.waitTime = waitTime;
        return this;
    }

    public int getRetransmitTimes() {
        return retransmitTimes;
    }

    public Config setRetransmitTimes(int retransmitTimes) {
        this.retransmitTimes = retransmitTimes;
        return this;
    }

    public int getPort() {
        return port;
    }

    public Config setPort(int port) {
        this.port = port;
        return this;
    }

    public int getTTL() {
        return TTL;
    }

    public Config setTTL(int TTL) {
        this.TTL = TTL;
        return this;
    }
}
