package model.util;

import java.nio.charset.StandardCharsets;

public class EncodeUtils {
    public static byte[] hex2Bytes(String hex) {
        hex = regularizeID(hex);
        byte[] data = new byte[Configs.ID_BYTES];
        for (int i = 0; i < Configs.ID_LEN; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i + 1), 16));
        }
        return data;
    }

    public static String bytes2HexStr(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            String strHex = Integer.toHexString(b & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex);
        }
        return removeLeadingZero(sb.toString());
    }

    public static String bytes2Str(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] str2Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }

    public static String regularizeID(String id) {
        if (id.length() == Configs.ID_LEN) {
            return id;
        }
        StringBuilder sb = new StringBuilder(Configs.ID_LEN);
        sb.append("00000000".repeat(32));
        sb.insert(Configs.ID_LEN - id.length(), id);
        return sb.toString();
    }

    public static String removeLeadingZero(String str) {
        int len = str.length(), i = 0;
        while (i < len && str.charAt(i) == '0') {
            i++;
        }
        return str.substring(i);
    }
}
