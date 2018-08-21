package com.rst.pkm.service;

import com.rst.pkm.common.Converter;
import lombok.Data;
import org.springframework.util.StringUtils;

/**
 * @author hujia
 */
public interface KeystoreService {
    @Data
    class Key {
        byte[] priv;
        byte[] pub;
        String serviceId;

        public Key(byte[] priv, byte[] pub, String serviceId) {
            this.priv = priv;
            this.pub = pub;
            this.serviceId = serviceId;
        }

        public Key(String priv, String pub, String serviceId) {
            this.priv = Converter.hexStringToByteArray(priv);
            this.pub = Converter.hexStringToByteArray(pub);
            this.serviceId = serviceId;
        }

        public String privHex() {
            return Converter.byteArrayToHexString(priv);
        }

        public String pubHex() {
            return Converter.byteArrayToHexString(pub);
        }

        @Override
        public String toString() {
            return privHex() + ":" + pubHex() + ":" +serviceId;
        }

        public static Key fromString(String value) {
            if (StringUtils.isEmpty(value)) {
                return null;
            }
            String[] values = value.split(":");
            if (values.length < 3) {
                return null;
            }
            return new Key(values[0], values[1], values[2]);
        }

    }

    void save(String serviceId, byte[] privateKey, byte[] publicKey);

    /**
     * 根据pubKeyHash获取公钥
     * @param pubKeyHash
     * @return
     */
     byte[] getPub(String pubKeyHash);

    /**
     * 根据pubKeyHash获取私钥
     * @param pubKeyHash
     * @return
     */
    byte[] getPriv(String pubKeyHash);
}
