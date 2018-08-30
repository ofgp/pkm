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
     * 添加白名单ip
     * @param serviceId
     * @param ip
     */
    void addValidIp(String serviceId, String ip);

    /**
     * 删除白名单ip
     * @param serviceId
     * @param ip
     */
    void delValidIp(String serviceId, String ip);

    /**
     * 显示白名单ip
     * @param serviceId
     * @return
     */
    String getService(String serviceId);

    /**
     * 锁定相关service的配置
     * @param serviceId
     */
    void lockService(String serviceId);

    /**
     * 解锁相关service的配置
     * @param serviceId
     */
    void unLockService(String serviceId);

    /**
     * 生成一段数据的签名信息
     * @param input
     * @param serviceId
     * @return
     */
    String generateSignature(String input, String serviceId);
}
