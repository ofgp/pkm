package com.rst.pkm.controller;

import com.rst.pkm.controller.interceptor.DisableSignatureCheck;
import com.rst.pkm.dto.response.CommonResult;
import com.rst.pkm.dto.response.ResGenerateService;
import com.rst.pkm.service.GodService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author hujia
 */
@Api(tags = "服务测试相关接口")
@RestController
@RequestMapping(value = "/god")
@DisableSignatureCheck
public class GodController {
    private final Logger logger = LoggerFactory.getLogger(GodController.class);
    @Autowired
    private GodService godService;

    @ApiOperation("生成service配置信息")
    @GetMapping("/createService")
    public String generateService() {
        logger.info("/createService");
        ResGenerateService resGenerateService = godService.generateService();
        return "serviceId: " + resGenerateService.getServiceId() + "\n"
                + "privateKey: " + resGenerateService.getPrivateKey() + "\n";
    }

    @ApiOperation("根据请求内容生成签名数据")
    @PostMapping("/createSignature")
    public CommonResult<String> generateSignature(
            @ApiParam(value = "请求server的serviceId", required = true) @RequestParam(value = "serviceId") String serviceId,
            @ApiParam(value = "待签名数据", required = true) @RequestParam(value = "input") String input
    ) {
        logger.info("/createSignature");
        return CommonResult.make(godService.generateSignature(input, serviceId));
    }
}
