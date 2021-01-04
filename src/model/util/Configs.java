package model.util;

public class Configs {
    public static final short DEFAULT_TIME_TO_LIVE = 2;
    public static final int DEFAULT_PORT_FOR_NET = 3080;
    public static final int DEFAULT_RETRANSMIT_TIMES = 5;
    public static final int DEFAULT_WAIT_TIME = 500;
    public static final Config DEFAULT_CONFIG = new Config()
            .setPort(DEFAULT_PORT_FOR_NET)
            .setTTL(DEFAULT_TIME_TO_LIVE)
            .setRetransmitTimes(DEFAULT_RETRANSMIT_TIMES)
            .setWaitTime(DEFAULT_WAIT_TIME);
    public static final int RSA_KEY_LEN = 1024;
    public static final int ID_LEN = RSA_KEY_LEN / 4;
    public static final int ID_BYTES = ID_LEN / 2;
    public static final int HASH_BYTES = 32;
}
