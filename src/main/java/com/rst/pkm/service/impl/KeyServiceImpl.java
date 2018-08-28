package com.rst.pkm.service.impl;

import com.rst.pkm.common.*;
import com.rst.pkm.service.KeyService;
import com.rst.pkm.service.KeystoreService;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;
import org.spongycastle.math.ec.ECPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

/**
 * @author hujia
 */
@Service
public class KeyServiceImpl implements KeyService {
    private final BtRandom secureRandom = new BtRandom();

    @Autowired
    private KeystoreService keystoreService;

    @Override
    public byte[] generateKey(String serviceId) {
        byte[] privateKey = secureRandom.nextBytes(32);
        byte[] publicKey = pubFromPriv(privateKey);
        keystoreService.save(serviceId, privateKey, publicKey);

        return publicKey;
    }

    private byte[] pubFromPriv(byte[] privateKey) {
        BigInteger privateIntValue = new BigInteger(1, privateKey);
        ECPoint multiply = Constant.CURVE.getG().multiply(privateIntValue);
        return multiply.getEncoded(false);
    }

    @Override
    public ECDSASignature sign(String inputHex, String pubHashHex, int type) {
        return ECDSAUtil.sign(Converter.hexStringToByteArray(inputHex), keystoreService.getPriv(pubHashHex), type);
    }

    @Override
    public boolean verify(byte[] hash, byte[] pub, byte[] signature) {
        ECDSASignature ecdsaSignature = ECDSASignature.decodeFromDER(signature);
        return verify(hash, pub, ecdsaSignature.r, ecdsaSignature.s);
    }

    @Override
    public boolean verify(String dataHex, String pubHashHex, String rHex, String sHex) {
        BigInteger r = new BigInteger(rHex, 16);

        return verify(Converter.hexStringToByteArray(dataHex),
                keystoreService.getPub(pubHashHex),
                new BigInteger(rHex, 16),
                new BigInteger(sHex,16));
    }

    private boolean verify(byte[] data, byte[] pub, BigInteger r, BigInteger s) {
        ECDSASigner signer = new ECDSASigner();
        signer.init(false, new ECPublicKeyParameters(Constant.CURVE.getCurve().decodePoint(pub),
                Constant.CURVE));
        return signer.verifySignature(data, r, s);
    }

    public static void main(String[] args) throws Exception {

    }
}
