package com.rst.pkm.controller;

import com.rst.pkm.common.Converter;
import com.rst.pkm.common.ECDSASignature;
import com.rst.pkm.config.CurrentThreadData;
import com.rst.pkm.controller.interceptor.DisableRequestCheck;
import com.rst.pkm.dto.request.ReqGenerateKey;
import com.rst.pkm.dto.request.ReqGenerateSignature;
import com.rst.pkm.dto.request.ReqVerifySignature;
import com.rst.pkm.dto.response.CommonResult;
import com.rst.pkm.dto.response.ResGenerateKey;
import com.rst.pkm.dto.response.ResGenerateSignature;
import com.rst.pkm.dto.response.ResVerifySignature;
import com.rst.pkm.service.KeyService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author hujia
 */
@DisableRequestCheck
@RestController
@Api(tags = "私钥相关接口")
@RequestMapping(value = "/key")
public class KeyController {
    @Autowired
    private KeyService keyService;

    @ApiOperation("生成count对公私钥")
    @PostMapping("/generate")
    public CommonResult<ResGenerateKey> generateKey(@Validated @RequestBody ReqGenerateKey body) {
        ResGenerateKey res = new ResGenerateKey();

        for (int i = 0; i < body.getCount(); i++) {
            res.getKeys().add(new ResGenerateKey.Key(
                    keyService.generateKey(CurrentThreadData.serviceId())));
        }

        return CommonResult.make(res);
    }

    @ApiOperation("生成签名数据：R + S")
    @PostMapping("/sign")
    public CommonResult<ResGenerateSignature> generateSignature(@Validated @RequestBody ReqGenerateSignature body) {
        ECDSASignature signature = keyService.sign(body.getInputHex(), body.getPubHashHex(), body.getType());
        ResGenerateSignature res = new ResGenerateSignature(signature);

        return CommonResult.make(res);
    }

    @ApiOperation("验证签名")
    @PostMapping("/verify")
    public CommonResult<ResVerifySignature> verifySignature(@Validated @RequestBody ReqVerifySignature body) {
        boolean verifyResult;
        if (StringUtils.isEmpty(body.getSignatureDerHex())) {
            verifyResult = keyService.verify(body.getInputHex(), body.getPubKeyHash(), body.getR(), body.getS());
        } else {
            verifyResult = keyService.verify(Converter.hexStringToByteArray(body.getInputHex()),
                    Converter.hexStringToByteArray(body.getPubKeyHash()),
                    Converter.hexStringToByteArray(body.getSignatureDerHex()));
        }

        return CommonResult.make(new ResVerifySignature(verifyResult));
    }
}
