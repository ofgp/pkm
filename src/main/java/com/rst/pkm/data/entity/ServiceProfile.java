package com.rst.pkm.data.entity;

import lombok.Data;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hujia
 */
@Data
public class ServiceProfile {
    private String serviceId;
    private String aesHex;
    private String privateKey;
    private String publicKey;
    private Set<String> allowIps;
    private int lockout;

    public static ServiceProfile from(ServiceProfile profile) {
        if (profile == null) {
            return null;
        }
        ServiceProfile newProfile = new ServiceProfile();
        newProfile.serviceId = profile.serviceId;
        newProfile.aesHex = profile.aesHex;
        newProfile.privateKey = profile.privateKey;
        newProfile.publicKey = profile.publicKey;
        if (profile.allowIps != null) {
            newProfile.allowIps = new HashSet<>();
            newProfile.allowIps.addAll(profile.allowIps);
        }

        newProfile.lockout = profile.lockout;
        return newProfile;
    }

    public boolean isLocked() {
        return lockout != 0;
    }
}
