package com.rst.pkm.service.impl;

import com.google.gson.GsonBuilder;
import com.rst.pkm.common.*;
import com.rst.pkm.common.Error;
import com.rst.pkm.controller.GodController;
import com.rst.pkm.controller.interceptor.CustomException;
import com.rst.pkm.data.dao.ServiceProfileDao;
import com.rst.pkm.data.entity.ServiceProfile;
import com.rst.pkm.dto.response.ResGenerateService;
import com.rst.pkm.service.GodService;
import com.rst.pkm.service.KeyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.math.ec.ECPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.*;

/**
 * @author hujia
 */
@Service
public class GodServiceImpl implements GodService {
    private final Logger logger = LoggerFactory.getLogger(GodController.class);

    private final BtRandom secureRandom = new BtRandom();

    @Autowired
    private ServiceProfileDao serviceProfileDao;
    @Autowired
    private KeyService keyService;

    @Override
    public ResGenerateService generateService() {
        String serviceId = UUID.randomUUID().toString();
        byte[] privateKey = secureRandom.nextBytes(32);
        BigInteger privateIntValue = new BigInteger(1, privateKey);
        ECPoint multiply = Constant.CURVE.getG().multiply(privateIntValue);
        byte[] publicKey = multiply.getEncoded(false);
        byte[] aesKey = secureRandom.nextBytes(16);

        ServiceProfile serviceProfile = new ServiceProfile();
        serviceProfile.setAesHex(Converter.byteArrayToHexString(aesKey));
        serviceProfile.setPrivateKey(Converter.byteArrayToHexString(privateKey));
        serviceProfile.setPublicKey(Converter.byteArrayToHexString(publicKey));
        serviceProfile.setServiceId(serviceId);
        serviceProfileDao.save(serviceProfile);

        ResGenerateService res = new ResGenerateService();
        res.setPrivateKey(Converter.byteArrayToHexString(privateKey));
        res.setServiceId(serviceId);

        return res;
    }

    @Override
    public void addValidIp(String serviceId, String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }

        ServiceProfile serviceProfile = profileFrom(serviceId);
        Set<String> allowIps = serviceProfile.getAllowIps();
        if (allowIps == null) {
            allowIps = new HashSet<>(1);
        }
        allowIps.add(ip.trim());
        serviceProfile.setAllowIps(allowIps);

        serviceProfileDao.save(serviceProfile);
    }

    @Override
    public void delValidIp(String serviceId, String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }

        ServiceProfile serviceProfile = profileFrom(serviceId);
        Set<String> allowIps = serviceProfile.getAllowIps();
        if (allowIps == null) {
            return;
        }

        allowIps.remove(ip.trim());
        serviceProfile.setAllowIps(allowIps);

        serviceProfileDao.save(serviceProfile);
    }

    @Override
    public String getService(String serviceId) {
        List<ServiceProfile> serviceProfiles = new ArrayList<>();
        if ("all".equalsIgnoreCase(serviceId)) {
            serviceProfiles = serviceProfileDao.findAll();
        } else {
            serviceProfiles.add(profileFrom(serviceId));
        }

        return new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .create().toJson(serviceProfiles).toString();
    }

    @Override
    public void lockService(String serviceId) {
        ServiceProfile serviceProfile = profileFrom(serviceId);
        serviceProfile.setLockout(1);
        serviceProfileDao.save(serviceProfile);
    }

    @Override
    public void unLockService(String serviceId) {
        ServiceProfile serviceProfile = profileFrom(serviceId);
        serviceProfile.setLockout(0);
        serviceProfileDao.save(serviceProfile);
    }

    @Override
    public String generateSignature(String input, String serviceId) {
        ServiceProfile serviceProfile = profileFrom(serviceId);

        ECDSASignature signature = ECDSAUtil.sign(
                Converter.sha256(input.getBytes()),
                Converter.hexStringToByteArray(serviceProfile.getPrivateKey()),
                0);

        return Converter.byteArrayToHexString(signature.encodeToDER());
    }

    private ServiceProfile profileFrom(String serviceId) {
        ServiceProfile serviceProfile = serviceProfileDao.findByServiceId(serviceId);
        if (serviceProfile == null) {
            CustomException.response(Error.SID_INVALID);
        }
        return serviceProfile;
    }
}
