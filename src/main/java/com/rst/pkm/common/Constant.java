package com.rst.pkm.common;

import org.spongycastle.asn1.x9.X9ECParameters;
import org.spongycastle.crypto.ec.CustomNamedCurves;
import org.spongycastle.crypto.params.ECDomainParameters;
import org.spongycastle.math.ec.FixedPointUtil;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.nio.charset.Charset;

/**
 * @author hujia
 */
@Component
public class Constant {
    public static final Charset DEFAULT_CHARSET = Charset.forName("UTF-8");
    public static final String SIGNATURE_HEADER = "signature";
    public static final String SERVICE_ID = "serviceId";

    /**
     * The parameters of the secp256k1 curve that Bitcoin uses.
     */
    public static final ECDomainParameters CURVE;
    public static final X9ECParameters CURVE_PARAMS = CustomNamedCurves.getByName("secp256k1");
    public static final BigInteger HALF_CURVE_ORDER;
    public static final BigInteger LARGEST_PRIVATE_KEY = new BigInteger
            ("FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEBAAEDCE6AF48A03BBFD25E8CD0364141", 16);
    public static final BigInteger Q = new BigInteger(
            "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFEFFFFFC2F", 16);

    static {
        FixedPointUtil.precompute(CURVE_PARAMS.getG(), 12);
        CURVE = new ECDomainParameters(CURVE_PARAMS.getCurve(), CURVE_PARAMS.getG(), CURVE_PARAMS.getN(),
                CURVE_PARAMS.getH());
        HALF_CURVE_ORDER = CURVE_PARAMS.getN().shiftRight(1);
    }


    public static final int SIGNATURE_TYPE_BTC = 0;
    public static final int SIGNATURE_TYPE_ETH = 1;
    public static final int SIGNATURE_TYPE_EOS = 2;

    public static byte[] ADMIN_KEY;

    public static void SET_ADMIN_KEY(String password) {
        byte[] adminBytes = Converter.ripemd160(password.getBytes());
        byte[] result = new byte[16];
        System.arraycopy(adminBytes, 0, result, 0, 16);

        ADMIN_KEY = result;
    }
}
