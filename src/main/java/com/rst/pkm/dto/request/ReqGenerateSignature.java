package com.rst.pkm.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hujia
 */
@Data
public class ReqGenerateSignature extends BaseRequest {
    @NotNull
    @ApiModelProperty("公钥hash,20字节的16进制字符串")
    private String pubHashHex;
    @NotNull
    @ApiModelProperty("待签名的数据，字节流的16进制字符串")
    private String inputHex;
    @ApiModelProperty("签名类型：0-类btc,1-类eth,2-eos")
    private int type = 0;
}
