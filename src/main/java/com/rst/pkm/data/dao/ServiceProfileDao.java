package com.rst.pkm.data.dao;

import com.google.gson.Gson;
import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Converter;
import com.rst.pkm.common.FileUtil;
import com.rst.pkm.data.entity.ServiceProfile;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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


        byte[] content = FileUtil.safeLoad(savePath + FILE_NAME);

        if (content == null || content.length <= 0) {
            logger.info("warning:none local data!");
            return;
        }

        content = Converter.hexStringToByteArray(new String(content));
        content = AESUtil.aesDecrypt(content, Constant.ADMIN_KEY);

        if (content == null) {
            logger.error("data or password incorrect!");
            return;
        }

        FileData fileData = new Gson().fromJson(new String(content), FileData.class);

        if (fileData == null || fileData.getServiceProfiles() == null) {
            return;
        }

        byte[] oldRandomKey = Converter.hexStringToByteArray(fileData.getRandomKey());
        fileData.getServiceProfiles().forEach(
                serviceProfile -> {
                    byte[] aesHexBytes = AESUtil.aesDecrypt(Converter.hexStringToByteArray(
                            serviceProfile.getAesHex()), oldRandomKey);

                    serviceProfile.setAesHex(new String(aesHexBytes));
                    serviceProfileMap.put(serviceProfile.getServiceId(), serviceProfile);
                });
    }

    /**
     * 通过serviceId获取service的资料信息
     * @param serviceId
     * @return
     */
    public ServiceProfile findByServiceId(String serviceId) {
        return serviceProfileMap.get(serviceId);
    }

    public List<ServiceProfile> findAll() {
        return serviceProfileMap.values().stream().collect(Collectors.toList());
    }

    public boolean save(ServiceProfile profile) {
        byte[] randomKey = FileUtil.loadRandomKey(savePath);
        byte[] encryptAesHexBytes = AESUtil.aesEncrypt(profile.getAesHex().getBytes(), randomKey);
        profile.setAesHex(Converter.byteArrayToHexString(encryptAesHexBytes));

        serviceProfileMap.put(profile.getServiceId(), profile);
        FileData data = new FileData();
        data.setRandomKey(Converter.byteArrayToHexString(randomKey));
        data.getServiceProfiles().addAll(serviceProfileMap.values());
        byte[] content = AESUtil.aesEncrypt(new Gson().toJson(data).getBytes(), Constant.ADMIN_KEY);
        boolean success = FileUtil.safeSave(
                Converter.byteArrayToHexString(content), savePath + FILE_NAME);

        AESUtil.wipe(randomKey);
        return success;
    }
}
