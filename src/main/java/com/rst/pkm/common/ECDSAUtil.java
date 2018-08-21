package com.rst.pkm.common;

import com.rst.pkm.controller.interceptor.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongycastle.crypto.digests.SHA256Digest;
import org.spongycastle.crypto.params.ECPrivateKeyParameters;
import org.spongycastle.crypto.params.ECPublicKeyParameters;
import org.spongycastle.crypto.signers.ECDSASigner;

import java.math.BigInteger;

/**
 * @author hujia
 */
public class ECDSAUtil {
    private final static Logger logger = LoggerFactory.getLogger(ECDSAUtil.class);
    private final static int SIGN_RETRY_COUNT = 25;

    public static ECDSASignature signEth(byte[] hash, byte[] privateKey) {
        int nonce = 0;
        while (nonce < SIGN_RETRY_COUNT){
            ECDSASignature signature = generateSignature(hash, privateKey, nonce++);
            signature.findRecoveryId(hash, privateKey);

            if (signature.recId > 0) {
                return signature;
            }
        }

        CustomException.response(com.rst.pkm.common.Error.NONE_CANONICAL_SIGNATURE);

        return null;
    }

    public static ECDSASignature signEos(byte[] hash, byte[] privateKey) {
        int nonce = 0;
        while (nonce < SIGN_RETRY_COUNT){
            ECDSASignature signature = generateSignature(hash, privateKey, nonce++);
            signature.findRecoveryId(hash, privateKey);

            if (signature.recId < 0) {
                continue;
            }

            byte[] compactSig = signature.compactForEOS(true);
            if (isEOSCanonical(compactSig)) {
                return signature;
            }
        }

        CustomException.response(com.rst.pkm.common.Error.NONE_CANONICAL_SIGNATURE);

        return null;
    }

    public static boolean isEOSCanonical(byte[] compactSig) {
        return (compactSig[1] & 0x80) == 0
                && !(compactSig[1] == 0 && ((compactSig[2] & 0x80) == 0))
                && (compactSig[33] & 0x80) == 0
                && !(compactSig[33] == 0 && ((compactSig[34] & 0x80) == 0));
    }

    public static ECDSASignature sign(byte[] hash, byte[] privateKey, int type) {
        if (type == Constant.SIGNATURE_TYPE_ETH) {
            return signEth(hash, privateKey);
        } else if (type == Constant.SIGNATURE_TYPE_EOS) {
            return signEos(hash, privateKey);
        } else {
            return generateSignature(hash, privateKey, 0);
        }
    }

    public static ECDSASignature generateSignature(byte[] hash, byte[] privateKey, int nonce) {
        BigInteger privateIntValue = new BigInteger(1, privateKey);

        ECDSASigner signer = new ECDSASigner(new HMacDSAKCalculatorEx(new SHA256Digest(), nonce));
        ECPrivateKeyParameters privateKeyParam = new ECPrivateKeyParameters(privateIntValue, Constant.CURVE);

        signer.init(true, privateKeyParam);

        BigInteger[] components = signer.generateSignature(hash);
        final ECDSASignature signature = new ECDSASignature(components[0], components[1]);
        signature.ensureCanonical();

        return signature;
    }

    public static boolean verify(byte[] data, ECDSASignature signature) {
        if (signature.recId < 0) {
            return false;
        }

        byte[] pub = ECDSASignature.recoverPubKey(data, signature, signature.recId);
        return verify(data, pub, signature.r, signature.s);
    }

    public static boolean verify(byte[] data, byte[] pub, BigInteger r, BigInteger s) {
        ECDSASigner signer = new ECDSASigner();
        signer.init(false, new ECPublicKeyParameters(Constant.CURVE.getCurve().decodePoint(pub),
                Constant.CURVE));

        return signer.verifySignature(data, r, s);
    }
}
