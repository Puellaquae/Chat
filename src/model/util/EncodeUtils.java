package model.util;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class EncodeUtils {
    public static byte[] hex2Bytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
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
        return sb.toString();
    }

    public static String bytes2Str(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public static byte[] str2Bytes(String str) {
        return str.getBytes(StandardCharsets.UTF_8);
    }
}
