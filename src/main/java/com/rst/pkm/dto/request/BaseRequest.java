package com.rst.pkm.dto.request;

import com.rst.pkm.dto.Readable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author hujia
 */
@Data
public class BaseRequest extends Readable {
    @NotNull
    @ApiModelProperty(value = "请求发出时的时间戳，1970-1-1至今的秒数", required = true)
    private long timestamp;
    @NotNull
    @ApiModelProperty(value = "请求server的专属ID")
    private String serviceId;
}
