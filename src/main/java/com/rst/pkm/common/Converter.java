package com.rst.pkm.common;

import org.spongycastle.crypto.digests.RIPEMD160Digest;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.jcajce.provider.digest.Keccak;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author hujia
 */
public class Converter {
    public static boolean containsHexPrefix(String input) {
        return !StringUtils.isEmpty(input) && input.length() > 1
                && input.charAt(0) == '0' && input.charAt(1) == 'x';
    }

    public static String cleanHexPrefix(String input) {
        if (containsHexPrefix(input)) {
            return input.substring(2);
        } else {
            return input;
        }
    }

    /**
     * 将16进制字符显示的字符数组转换成对应的数字的字节数组
     * @param input 待转化的字符数组
     * @return 转化后的字节数组
     */
    public static byte[] hexStringToByteArray(String input)
    {
        String cleanInput = cleanHexPrefix(input);

        int len = cleanInput.length();

        if (len == 0) {
            return new byte[] {};
        }

        byte[] data;
        int startIdx;
        if (len % 2 != 0) {
            data = new byte[(len / 2) + 1];
            data[0] = (byte) Character.digit(cleanInput.charAt(0), 16);
            startIdx = 1;
        } else {
            data = new byte[len / 2];
            startIdx = 0;
        }

        for (int i = startIdx; i < len; i += 2) {
            data[(i + 1) / 2] = (byte) ((Character.digit(cleanInput.charAt(i), 16) << 4)
                    + Character.digit(cleanInput.charAt(i + 1), 16));
        }
        return data;
    }

    /**
     * 将数字对应的字节数组转换成16进制显示的字符数组
     * @param a 将数字对应的字节数组
     * @return 转化后的16进制字符串
     */
    public static String byteArrayToHexString(byte[] a)
    {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b: a) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    public static byte[] sha256(byte[] bytes) {
        return sha256(bytes, 0, bytes.length);
    }

    public static byte[] sha256(byte[] bytes, int offset, int size) {
        SHA256Digest sha256Digest = new SHA256Digest();
        sha256Digest.update(bytes, offset, size);
        byte[] sha256 = new byte[32];
        sha256Digest.doFinal(sha256, 0);
        return sha256;
    }

    public static byte[] sha256(byte[]... byteArrays) {
        SHA256Digest sha256Digest = new SHA256Digest();
        for (byte[] byteArray : byteArrays) {
            sha256Digest.update(byteArray, 0, byteArray.length);
        }

        byte[] sha256 = new byte[32];
        sha256Digest.doFinal(sha256, 0);

        return sha256;
    }

    public static byte[] doubleSha256(byte[] bytes) {
        return doubleSha256(bytes, 0, bytes.length);
    }

    public static byte[] doubleSha256(byte[] bytes, int offset, int size) {
        try {
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            sha256.update(bytes, offset, size);
            return sha256.digest(sha256.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static final int RIPEMD160_DIGEST_LENGTH = 20;

    public static byte[] ripemd160(byte[] bytes) {
        RIPEMD160Digest ripemd160Digest = new RIPEMD160Digest();
        ripemd160Digest.update(bytes, 0, bytes.length);
        byte[] hash160 = new byte[RIPEMD160_DIGEST_LENGTH];
        ripemd160Digest.doFinal(hash160, 0);
        return hash160;
    }

    public static byte[] hash160(final byte[] bytes) {
        return ripemd160(sha256(bytes));
    }

    private static final String HMAC_SHA512 = "HmacSHA512";
    private static final String HMAC_SHA256 = "HmacSHA256";

    public static byte[] hmacSha256(final byte[] byteKey, final byte[] seed) {
        return hmacSha(byteKey, seed, HMAC_SHA256);
    }

    public static byte[] hmacSha512(final byte[] byteKey, final byte[] seed) {
        return hmacSha(byteKey, seed, HMAC_SHA512);
    }

    public static byte[] hmacSha(final byte[] byteKey, final byte[] seed, String hmac) {
        try {
            Mac hmacSha = Mac.getInstance(hmac);
            SecretKeySpec keySpec = new SecretKeySpec(byteKey, hmac);
            hmacSha.init(keySpec);
            return hmacSha.doFinal(seed);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Keccak-256 hash function.
     *
     * @param input binary encoded input data
     * @param offset of start of data
     * @param length of data
     * @return hash value
     */
    public static byte[] sha3(byte[] input, int offset, int length) {
        Keccak.DigestKeccak kecc = new Keccak.Digest256();
        kecc.update(input, offset, length);
        return kecc.digest();
    }

    /**
     * Keccak-256 hash function.
     *
     * @param input binary encoded input data
     * @return hash value
     */
    public static byte[] sha3(byte[] input) {
        return sha3(input, 0, input.length);
    }

    public static byte[] toBigEndianBytes(BigInteger value, int length) {
        byte[] result = new byte[length];
        byte[] bytes = value.toByteArray();

        int bytesLength;
        int srcOffset;
        if (bytes[0] == 0) {
            bytesLength = bytes.length - 1;
            srcOffset = 1;
        } else {
            bytesLength = bytes.length;
            srcOffset = 0;
        }

        if (bytesLength > length) {
            throw new RuntimeException("Input is too large to put in byte array of size " + length);
        }

        int destOffset = length - bytesLength;
        System.arraycopy(bytes, srcOffset, result, destOffset, bytesLength);
        return result;
    }

    public static byte[] toBigEndianBytes(String number, int length) {
        return toBigEndianBytes(new BigInteger(number), length);
    }

    public static byte[] toBigEndianBytes(int value, int length) {
        byte[] result = new byte[length];

        int start = 0;
        if (length > 4) {
            start = length - 4;
        }

        for (int i = start; i < length; i++) {
            result[i] = (byte)(value >> (8 * (length - i - 1)) & 0xFF);
        }

        return result;
    }

    public static byte[] toBigEndianBytes(long value, int length) {
        byte[] result = new byte[length];
        int start = 0;
        if (length > 8) {
            start = length - 8;
        }

        for (int i = start; i < length; i++) {
            result[i] = (byte)(value >> (8 * (length - i - 1)) & 0xFF);
        }

        return result;
    }
}
