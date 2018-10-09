package com.rst.pkm.dto.response;

import com.rst.pkm.common.Converter;
import com.rst.pkm.common.ECDSASignature;
import com.rst.pkm.dto.Readable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hujia
 */
@Data
public class  ResGenerateSignature extends Readable {
    @ApiModelProperty(value = "der编码序列化的签名(BTC/BCH)")
    private String signatureDerHex;
    @ApiModelProperty(value = "R值, 16进制字符串代表的数值")
    private String R;
    @ApiModelProperty(value = "S值，16进制字符串代表的数值")
    private String S;
    @ApiModelProperty(value = "pub key recovery值:0,1,2,3为可用值")
    private int recId;
    @ApiModelProperty(value = "序列化的Signature(EOS):一字节前缀(27+4+recId)+32字节R（高位0padding）+32字节S（高位0padding）")
    private String signatureCompactHex;

    public ResGenerateSignature(ECDSASignature signature) {
        R = signature.r.toString(16);
        S = signature.s.toString(16);
        recId = signature.recId;
        signatureDerHex = Converter.byteArrayToHexString(signature.encodeToDER());
        signatureCompactHex = Converter.byteArrayToHexString(signature.compactForEOS(true));
    }
}
