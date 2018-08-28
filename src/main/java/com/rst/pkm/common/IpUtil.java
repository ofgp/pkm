package com.rst.pkm.common;


import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author hujia
 */
public class IpUtil {

    /**
     * 获取请求的IP地址
     * @param request
     * @return
     */

    public static String clientIpFrom(HttpServletRequest request) {
        String ipAddress;
        do {
            //从Nginx中X-Real-IP获取真实ip
            ipAddress = request.getHeader("X-Real-IP");
            if (isValid(ipAddress)) {
                break;
            }

            //从Nginx中x-forwarded-for获取真实ip
            ipAddress = request.getHeader("x-forwarded-for");
            if (isValid(ipAddress)) {
                break;
            }

            //从Apache中Proxy-Client-IP获取真实ip
            ipAddress = request.getHeader("Proxy-Client-IP");
            if (isValid(ipAddress)) {
                break;
            }

            //从WebLogic中WL-Proxy-Client-IP获取真实ip
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
            if (isValid(ipAddress)) {
                break;
            }
            //直接取
            ipAddress = request.getRemoteAddr();

            if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                    break;
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
            }

            if (!isValid(ipAddress)) {
                return "unknown";
            }
        } while (true);


        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        int index = ipAddress.indexOf(",");
        if (index > 0) {
            ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
        }

        return ipAddress;
    }

    public static boolean isValid(String ipAddress) {
        return ipAddress != null && ipAddress.length() > 0 && !"unknown".equalsIgnoreCase(ipAddress);
    }
}
