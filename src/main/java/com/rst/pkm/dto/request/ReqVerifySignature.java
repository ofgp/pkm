package com.rst.pkm.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hujia
 */
@Data
public class ReqVerifySignature extends BaseRequest {
    @ApiModelProperty(value = "der编码序列化的签名RS，此项不空时以此校验签名")
    private String signatureDerHex;
    @ApiModelProperty(value = "R值(16进制字符串), signatureDerHex不传或为空时有效")
    private String R;
    @ApiModelProperty(value = "S值(16进制字符串)，signatureDerHex不传或为空时有效")
    private String S;
    @ApiModelProperty(value = "被签名的数据流(16进制表示)")
    private String inputHex;
    @ApiModelProperty(value = "公钥hash")
    private String pubKeyHash;
}
