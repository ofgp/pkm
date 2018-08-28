package com.rst.pkm.service.impl;

import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Error;
import com.rst.pkm.common.IpUtil;
import com.rst.pkm.controller.interceptor.CustomException;
import com.rst.pkm.data.entity.ServiceProfile;
import com.rst.pkm.data.dao.ServiceProfileDao;
import com.rst.pkm.service.RequestControlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

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
        if (serviceProfile == null) {
            CustomException.response(Error.SID_INVALID);
        }

        String fromIp = IpUtil.clientIpFrom(request);
        if (serviceProfile.getAllowIps() != null &&
                !serviceProfile.getAllowIps().isEmpty() &&
                !serviceProfile.getAllowIps().contains(MATCH_ALL_IP)
                && !serviceProfile.getAllowIps().contains(fromIp)) {
            CustomException.response(Error.IP_INVALID);
        }
    }
}
