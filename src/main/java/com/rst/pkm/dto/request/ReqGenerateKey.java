package com.rst.pkm.dto.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Min;

/**
 * @author hujia
 */
@Data
public class ReqGenerateKey extends BaseRequest {
    @Min(1)
    @ApiModelProperty("生成count对公私钥")
    private int count;
}
