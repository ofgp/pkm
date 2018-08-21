package com.rst.pkm.controller.interceptor;

import com.rst.pkm.common.SpringUtil;
import com.rst.pkm.service.RequestControlService;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 *
 * @author hujia
 * @date 2017/3/30
 */
public class RequestCheckInterceptor implements HandlerInterceptor {

    public RequestCheckInterceptor() {
        requestControlService = SpringUtil.getBean(RequestControlService.class);
    }

    private RequestControlService requestControlService;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (handler != null && handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Class beanType = handlerMethod.getBeanType();
            if (beanType.isAnnotationPresent(DisableRequestCheck.class)) {
                return true;
            }

            Method method = handlerMethod.getMethod();
            if (method.isAnnotationPresent(DisableRequestCheck.class)) {
                return true;
            }
        }

        //do some check
        requestControlService.check(request);

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
