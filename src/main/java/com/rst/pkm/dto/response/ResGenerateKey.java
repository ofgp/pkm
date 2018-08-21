package com.rst.pkm.dto.response;

import com.rst.pkm.common.Converter;
import com.rst.pkm.dto.Readable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hujia
 */
@Data
public class ResGenerateKey extends Readable {
    @Data
    public static class Key {
        @ApiModelProperty(value = "非压缩公钥，16进制字符串")
        public String pubHex;
        @ApiModelProperty(value = "非压缩公钥的hash，16进制字符串")
        public String pubHashHex;

        public Key(byte[] pub) {
            this.pubHex = Converter.byteArrayToHexString(pub);
            this.pubHashHex = Converter.byteArrayToHexString(Converter.ripemd160(Converter.sha256(pub)));
        }

        public Key(String pubHex) {
            this.pubHex = pubHex;
            this.pubHashHex = Converter.byteArrayToHexString(
                    Converter.ripemd160(
                            Converter.sha256(
                                    Converter.hexStringToByteArray(pubHex))));
        }
    }

    @ApiModelProperty(value = "一组公私钥对")
    private List<Key> keys = new ArrayList<>();
}
