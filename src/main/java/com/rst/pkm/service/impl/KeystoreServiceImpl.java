package com.rst.pkm.service.impl;

import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Converter;
import com.rst.pkm.common.Error;
import com.rst.pkm.controller.interceptor.CustomException;
import com.rst.pkm.data.dao.ServiceKeyDao;
import com.rst.pkm.data.dao.ServiceProfileDao;
import com.rst.pkm.data.entity.ServiceKey;
import com.rst.pkm.data.entity.ServiceProfile;
import com.rst.pkm.service.KeystoreService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author hujia
 */
@Service
public class KeystoreServiceImpl implements KeystoreService {
    private final Logger logger = LoggerFactory.getLogger(KeystoreServiceImpl.class);

    @Autowired
    private ServiceKeyDao skDao;
    @Autowired
    private ServiceProfileDao spRepository;

    @Override
    public void save(String serviceId, byte[] privateKey, byte[] publicKey) {
        ServiceProfile sp = spRepository.findByServiceId(serviceId);

        byte[] aesKey = Converter.hexStringToByteArray(sp.getAesHex());
        byte[] privEncrypt = AESUtil.aesEncrypt(privateKey, aesKey);

        ServiceKey serviceKey = new ServiceKey();
        serviceKey.setPrivKey(Converter.byteArrayToHexString(privEncrypt));
        serviceKey.setPubKey(Converter.byteArrayToHexString(publicKey));
        String pubHashHex = Converter.byteArrayToHexString(
                Converter.ripemd160(Converter.sha256(publicKey)));
        serviceKey.setPubKeyHash(pubHashHex);
        serviceKey.setServiceId(serviceId);

        skDao.save(serviceKey);
    }

    @Override
    public byte[] getPub(String pubKeyHash) {
        Key key = get(pubKeyHash);
        if (key == null) {
            logger.info("can not find public key");
            return new byte[0];
        }
        return key.getPub();
    }

    @Override
    public byte[] getPriv(String pubKeyHash) {
        Key key = get(pubKeyHash);
        if (key == null) {
            logger.info("can not find private key");
            return new byte[0];
        }

        ServiceProfile sp = spRepository.findByServiceId(key.getServiceId());

        return AESUtil.aesDecrypt(key.getPriv(), Converter.hexStringToByteArray(sp.getAesHex()));
    }

    Key fromDb(String pubKeyHash) {
        ServiceKey sk = skDao.findByPubHash(pubKeyHash);
        if (sk == null) {
            CustomException.response(Error.PUB_HASH_INVALID);
        }
        Key key = new Key(sk.getPrivKey(), sk.getPubKey(), sk.getServiceId());

        return key;
    }

    Key get(String pubKeyHash) {
        return fromDb(pubKeyHash);
    }
}
