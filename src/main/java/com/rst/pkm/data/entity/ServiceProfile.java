package com.rst.pkm.data.entity;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author hujia
 */
@Data
public class ServiceProfile {
    private String serviceId;
    private String aesHex;
    private String privateKey;
    private String publicKey;
    private List<String> allowIps;

    public static ServiceProfile from(ServiceProfile profile) {
        if (profile == null) {
            return null;
        }
        ServiceProfile newProfile = new ServiceProfile();
        newProfile.serviceId = profile.serviceId;
        newProfile.aesHex = profile.aesHex;
        newProfile.privateKey = profile.privateKey;
        newProfile.publicKey = profile.publicKey;
        if (profile.allowIps != null && !profile.allowIps.isEmpty()) {
            newProfile.allowIps = new ArrayList<>();
            newProfile.allowIps.addAll(profile.allowIps);
        }
        return newProfile;
    }
}
