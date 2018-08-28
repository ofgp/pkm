package com.rst.pkm.data.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author hujia
 */
@Data
public class ServiceKey {
    private Integer id;
    private String serviceId;
    private String privKey;
    private String pubKey;
    private String pubKeyHash;
    private Date CreateDate;
}
