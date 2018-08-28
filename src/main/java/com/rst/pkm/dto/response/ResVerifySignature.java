package com.rst.pkm.dto.response;

import com.rst.pkm.dto.Readable;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author hujia
 */
public class ResVerifySignature extends Readable {
    @ApiModelProperty("true表示验证通过")
    boolean result;

    public ResVerifySignature(boolean result) {
        this.result = result;
    }
}
