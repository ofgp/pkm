package com.rst.pkm.dto.response;

import com.rst.pkm.common.Error;
import com.rst.pkm.dto.Readable;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 通用的返回结果
 * @author hujia
 * @date 2017/2/28
 */
@Data
public class CommonResult<T> extends Readable {
    @ApiModelProperty("返回码，一般0代表无错误")
	private int code = 0;
    @ApiModelProperty("一般表示错误信息")
    private String msg = "";
    @ApiModelProperty("返回的数据内容")
	private T data;

    public CommonResult() {}

    public CommonResult(final String msg) {
        this.code = -1;
        this.msg = msg;
    }

    public CommonResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public CommonResult(int code, String msg, T data) {
		super();
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public void setError(Error error) {
        code = error.code;
        msg = error.msg;
    }

    public static  <T> CommonResult<T> make(Error error) {
        CommonResult<T> result = new CommonResult<>();
        result.code = error.code;
        result.msg = error.msg;
        return result;
    }

    public static  <T> CommonResult<T> make(T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = 0;
        result.msg = "";
        result.data = data;
        return result;
    }

    public static  <T> CommonResult<T> make(int code, String msg, T data) {
        CommonResult<T> result = new CommonResult<>();
        result.code = code;
        result.msg = msg;
        result.data = data;
        return result;
    }
}
