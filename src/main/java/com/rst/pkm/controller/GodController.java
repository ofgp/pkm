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

    @ApiOperation("生成service配置信息")
    @PostMapping("/allowIp/{op}/{serviceId}/{ip}")
    public String allowIp(
            @ApiParam(value = "添加/删除白名单ip", required = true) @PathVariable("op") int op,
            @ApiParam(value = "添加/删除白名单ip", required = true) @PathVariable("serviceId") String serviceId,
            @ApiParam(value = "添加/删除白名单ip") @PathVariable(value = "ip") String ip
    ) {
        logger.info("/allowIp");
        if (op == 0) {
            godService.addValidIp(serviceId, ip);
        } else if (op == 1) {
            godService.delValidIp(serviceId, ip);
        } else if (op == 2) {
            return godService.getService(serviceId) + "\n";
        } else if (op == 3) {
            godService.lockService(serviceId);
        } else if (op == 4) {
            godService.unLockService(serviceId);
        } else  {
            return "unsupported op\n";
        }

        return "op success!\n";
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
