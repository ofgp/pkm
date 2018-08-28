package com.rst.pkm.service;

import com.rst.pkm.dto.response.ResGenerateService;

/**
 * @author hujia
 */
public interface GodService {
    /**
     * 生成一个允许访问的server配置信息
     * @return
     */
    ResGenerateService generateService();

    /**
     * 生成一段数据的签名信息
     * @param input
     * @param serviceId
     * @return
     */
    String generateSignature(String input, String serviceId);
}
