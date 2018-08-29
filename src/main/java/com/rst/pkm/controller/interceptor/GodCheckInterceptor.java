package com.rst.pkm.controller.interceptor;

import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Error;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * @author hujia
 */
public class GodCheckInterceptor implements HandlerInterceptor {
    private static final String VALID_IP = "127.0.0.1";
    private static final String ADMIN_PASSWORD = "password";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String fromIp = request.getRemoteAddr();

        //只允许以127.0.0.1方式直接访问
        if (!VALID_IP.equals(fromIp)) {
            CustomException.response(Error.IP_INVALID);
        }

        String password = request.getHeader(ADMIN_PASSWORD);
        if (StringUtils.isEmpty(password) ||
                !Arrays.equals(AESUtil.aesKeyFrom(password), Constant.ADMIN_KEY)) {
            CustomException.response(Error.ADMIN_PWD_INVALID);
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {

    }
}
