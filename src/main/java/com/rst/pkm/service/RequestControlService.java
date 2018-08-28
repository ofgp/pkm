package com.rst.pkm.service;

import javax.servlet.http.HttpServletRequest;

/**
 * @author hujia
 */
public interface RequestControlService {
    /**
     * 请求合法性校验
     * @param request
     */
    void check(HttpServletRequest request);
}
