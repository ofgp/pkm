package com.rst.pkm.service.impl;

import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Error;
import com.rst.pkm.common.IpUtil;
import com.rst.pkm.controller.interceptor.CustomException;
import com.rst.pkm.data.dao.ServiceProfileDao;
import com.rst.pkm.data.entity.ServiceProfile;
import com.rst.pkm.service.RequestControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Set;

/**
 * @author hujia
 */
@Service
public class RequestControlServiceImpl implements RequestControlService {

    private final String MATCH_ALL_IP = "*";
    @Autowired
    private ServiceProfileDao spRepository;
    @Override
    public void check(HttpServletRequest request) {
        String serviceId = request.getHeader(Constant.SERVICE_ID);
        if (StringUtils.isEmpty(serviceId)) {
            CustomException.response(Error.SID_NOT_PRESENT);
        }

        ServiceProfile serviceProfile = spRepository.findByServiceId(serviceId);
        if (serviceProfile == null || serviceProfile.isLocked()) {
            CustomException.response(Error.SID_INVALID);
        }

        checkIp(request.getRemoteAddr(), serviceProfile.getAllowIps());
        checkIp(IpUtil.clientIpFrom(request), serviceProfile.getAllowIps());
    }

    private void checkIp(String ip, Set<String> allowIps) {
        if (allowIps == null) {
            return;
        }

        if (!allowIps.contains(MATCH_ALL_IP) && !allowIps.contains(ip)) {
            CustomException.response(Error.IP_INVALID);
        }
    }
}
