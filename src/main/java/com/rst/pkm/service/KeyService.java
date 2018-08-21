package com.rst.pkm.service;

import com.rst.pkm.common.ECDSASignature;

/**
 * @author hujia
 */
public interface KeyService {
    /**
     * 生成私钥/公钥对
     * @param serviceId 服务方Id
     * @return 520bit非压缩公钥或264bit压缩公钥
     */
    byte[] generateKey(String serviceId);

    /**
     * 生成指定数据的签名R值和S值
     * @param inputHex 待签数据
     * @param pubHashHex 签名私钥对应的公钥hash
     * @param type 签名类型：0-btc,1-eth,2-eos
     * @return 签名数据
     */
    ECDSASignature sign(String inputHex, String pubHashHex, int type);

    /**
     * 验证签名
     * @param hash 被签名的数据
     * @param signature der编码的签名
     * @param pub 签名对应的公钥
     * @return true为通过
     */
    boolean verify(byte[] hash, byte[] pub, byte[] signature);

    /**
     * 验证签名
     * @param dataHex 被签名的数据
     * @param pubHashHex 公钥hash
     * @param rHex 签名R值
     * @param sHex 签名S值
     * @return true为通过
     */
    boolean verify(String dataHex, String pubHashHex, String rHex, String sHex);
}
