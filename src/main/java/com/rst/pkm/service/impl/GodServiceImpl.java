package com.rst.pkm.service.impl;

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
import org.spongycastle.asn1.sec.SECNamedCurves;
import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.ECPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.UUID;

/**
 * @author hujia
 */
@Service
public class GodServiceImpl implements GodService {
    private final Logger logger = LoggerFactory.getLogger(GodController.class);

    private final BtRandom secureRandom = new BtRandom();
    private final X9ECParameters CURVE = SECNamedCurves.getByName("secp256k1");
    private final ECDomainParameters domain = new ECDomainParameters(CURVE.getCurve(),
            CURVE.getG(), CURVE.getN(), CURVE.getH());
    private final BigInteger LARGEST_PRIVATE_KEY = new BigInteger
            ("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    private final BigInteger LARGEST_S = new BigInteger
            ("7FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF5D576E7357A4501DDFE92F46681B20A0", 16);

    @Autowired
    private ServiceProfileDao serviceProfileDao;
    @Autowired
    private KeyService keyService;

    @Override
    public ResGenerateService generateService() {
        String serviceId = UUID.randomUUID().toString();
        byte[] privateKey = secureRandom.nextBytes(32);
        BigInteger privateIntValue = new BigInteger(1, privateKey);
        ECPoint multiply = CURVE.getG().multiply(privateIntValue);
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
    public String generateSignature(String input, String serviceId) {
        ServiceProfile serviceProfile = serviceProfileDao.findByServiceId(serviceId);
        if (serviceProfile == null) {
            CustomException.response(Error.SID_INVALID);
        }

        ECDSASignature signature = ECDSAUtil.sign(
                Converter.sha256(input.getBytes()),
                Converter.hexStringToByteArray(serviceProfile.getPrivateKey()),
                0);

        return Converter.byteArrayToHexString(signature.encodeToDER());
    }
}
