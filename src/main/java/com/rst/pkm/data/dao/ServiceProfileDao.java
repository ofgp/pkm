package com.rst.pkm.data.dao;

import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Converter;
import com.rst.pkm.common.FileUtil;
import com.rst.pkm.data.entity.ServiceProfile;
import com.google.gson.Gson;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hujia
 */
@Component
public class ServiceProfileDao {
    private final Logger logger = LoggerFactory.getLogger(ServiceProfileDao.class);
    private Map<String, ServiceProfile> serviceProfileMap;
    @Value("${pkm.data-path}")
    private String savePath;

    public static final String FILE_NAME = "service-profile";
    public static final String BACKUP_FILE_NAME = "backup/service-profile";

    @Data
    private class FileData {
        private String randomKey;
        private List<ServiceProfile> serviceProfiles = new ArrayList<>();
    }

    @PostConstruct
    void init() {
        serviceProfileMap = new HashMap<>();

        if (!savePath.endsWith(File.separator)) {
            savePath += File.separator;
        }


        byte[] content = FileUtil.load(savePath + FILE_NAME);
        if (content == null || content.length <= 0) {
            content = FileUtil.load(savePath + BACKUP_FILE_NAME);
        }

        if (content == null || content.length <= 0) {
            return;
        }

        content = Converter.hexStringToByteArray(new String(content));
        content = AESUtil.aesDecrypt(content, Constant.ADMIN_KEY);

        if (content == null) {
            logger.error("local data or password incorrect!");
            return;
        }

        FileData fileData = new Gson().fromJson(new String(content), FileData.class);

        if (fileData == null || fileData.getServiceProfiles() == null) {
            return;
        }

        byte[] randomKey = FileUtil.loadRandomKey(savePath);
        byte[] oldRandomKey = Converter.hexStringToByteArray(fileData.getRandomKey());
        fileData.getServiceProfiles().forEach(
                serviceProfile -> {
                    byte[] aesHexBytes = AESUtil.aesDecrypt(Converter.hexStringToByteArray(
                            serviceProfile.getAesHex()), oldRandomKey);

                    serviceProfile.setAesHex(Converter.byteArrayToHexString(AESUtil.aesEncrypt(aesHexBytes, randomKey)));
                    serviceProfileMap.put(serviceProfile.getServiceId(), serviceProfile);
                });

        AESUtil.wipe(randomKey);
    }

    /**
     * 通过serviceId获取service的资料信息
     * @param serviceId
     * @return
     */
    public ServiceProfile findByServiceId(String serviceId) {
        ServiceProfile profile = ServiceProfile.from(serviceProfileMap.get(serviceId));

        if (profile != null && !StringUtils.isEmpty(profile.getAesHex())) {
            byte[] randomKey = FileUtil.loadRandomKey(savePath);
            byte[] aesHexBytes = AESUtil.aesDecrypt(Converter.hexStringToByteArray(profile.getAesHex()), randomKey);
            AESUtil.wipe(randomKey);
            profile.setAesHex(new String(aesHexBytes));
        }

        return profile;
    }

    public List<ServiceProfile> findAll() {
        byte[] randomKey = FileUtil.loadRandomKey(savePath);
        List<ServiceProfile> result = serviceProfileMap.values().stream().map(item -> {
            ServiceProfile profile = ServiceProfile.from(item);
            if (profile != null && !StringUtils.isEmpty(profile.getAesHex())) {
                byte[] aesHexBytes = AESUtil.aesDecrypt(Converter.hexStringToByteArray(profile.getAesHex()), randomKey);
                profile.setAesHex(new String(aesHexBytes));
            }
            return profile;
        }).collect(Collectors.toList());
        AESUtil.wipe(randomKey);
        return result;
    }

    public boolean save(ServiceProfile profile) {
        byte[] randomKey = FileUtil.loadRandomKey(savePath);
        byte[] encryptAesHexBytes = AESUtil.aesEncrypt(profile.getAesHex().getBytes(), randomKey);
        profile.setAesHex(Converter.byteArrayToHexString(encryptAesHexBytes));

        serviceProfileMap.put(profile.getServiceId(), profile);
        FileData data = new FileData();
        data.setRandomKey(Converter.byteArrayToHexString(randomKey));
        data.getServiceProfiles().addAll(serviceProfileMap.values());
        boolean success;
        byte[] content = AESUtil.aesEncrypt(new Gson().toJson(data).getBytes(), Constant.ADMIN_KEY);
        success = FileUtil.save(Converter.byteArrayToHexString(content), savePath + FILE_NAME);
        if (success) {
            success = FileUtil.backup(savePath + FILE_NAME, savePath + BACKUP_FILE_NAME);
        }

        AESUtil.wipe(randomKey);
        return success;
    }
}
