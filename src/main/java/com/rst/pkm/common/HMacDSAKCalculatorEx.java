package com.rst.pkm.common;

import org.spongycastle.crypto.Digest;
import org.spongycastle.crypto.signers.HMacDSAKCalculator;

import java.math.BigInteger;

/**
 * @author hujia
 */
public class HMacDSAKCalculatorEx extends HMacDSAKCalculator {
    private int nonce;

    public HMacDSAKCalculatorEx(Digest digest) {
        super(digest);
        this.nonce = 0;
    }

    public HMacDSAKCalculatorEx(Digest digest, int nonce) {
        super(digest);
        this.nonce = nonce;
    }

    @Override
    public void init(BigInteger n, BigInteger d, byte[] message) {
        if ( nonce > 0 ){
            message = Converter.sha256(message, BtRandom.getSeed(nonce));
        }

        super.init(n, d, message);
    }
}
