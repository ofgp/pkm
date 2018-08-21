package com.rst.pkm.controller.interceptor;

import com.rst.pkm.common.Error;
import com.rst.pkm.common.IpUtil;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hujia
 */
public class GodCheckInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        String fromIp = IpUtil.clientIpFrom(request);

        InetAddress inet = null;

        try {
            inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        if ("127.0.0.1".equals(fromIp)
                || "0:0:0:0:0:0:0:1".equals(fromIp)
                || inet.getHostAddress().equalsIgnoreCase(fromIp)) {
            return true;
        } else {
            CustomException.response(Error.IP_INVALID);
        }

        return false;
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
