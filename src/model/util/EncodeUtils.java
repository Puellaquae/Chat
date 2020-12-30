package model.util;

import java.nio.charset.StandardCharsets;

public class EncodeUtils {
    public static byte[] hex2Bytes(String hex) {
        hex = regularizeID(hex);
        byte[] data = new byte[128];
        for (int i = 0; i < 256; i += 2) {
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
        if (id.length() == 256) {
            return id;
        }
        StringBuilder sb = new StringBuilder(256);
        sb.append("00000000".repeat(32));
        sb.insert(256 - id.length(), id);
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
