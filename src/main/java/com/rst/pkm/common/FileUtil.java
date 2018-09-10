package com.rst.pkm.common;

import com.google.common.io.Files;
import org.springframework.util.StringUtils;

import java.io.*;

/**
 * @author hujia
 */
public class FileUtil {
    public static boolean safeSave(String content, String fileName) {
        return safeSave(content, fileName, defaultBackupName(fileName));
    }

    public static boolean safeSave(String content, String fileName, String backupFileName) {
        boolean success = FileUtil.save(content, fileName);
        if (success) {
            success = FileUtil.copy(fileName, backupFileName);
        } else {
            FileUtil.copy(backupFileName, fileName);
        }

        return success;
    }

    public static byte[] safeLoad(String fileName) {
        byte[] content = FileUtil.load(fileName);
        if (content == null || content.length <= 0) {
            content = FileUtil.load(defaultBackupName(fileName));
        }

        return content;
    }

    public static byte[] safeLoad(String fileName, String backupFileName) {
        byte[] content = FileUtil.load(fileName);
        if (content == null || content.length <= 0) {
            content = FileUtil.load(backupFileName);
        }

        return content;
    }

    private static String defaultBackupName(String fileName) {
        if (StringUtils.isEmpty(fileName)) {
            return fileName;
        }
        File file = new File(fileName);
        String parent = file.getParent();
        String name = file.getName();
        String backupFileName = name;
        if (StringUtils.isEmpty(parent)) {
            backupFileName = "backup" + File.separator + backupFileName;
        } else {
            backupFileName = parent + "backup" + File.separator + backupFileName;
        }

        return backupFileName;
    }

    public static boolean save(String content, String fileName) {
        File file = new File(fileName);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        try {
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter writer=new FileWriter(fileName);
            writer.write(content);
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean copy(String src, String dest) {
        File srcfile = new File(src);
        if (!srcfile.exists()) {
            return false;
        }

        File destFile = new File(dest);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        } else if (destFile.exists()) {
            destFile.delete();
        }

        try {
            Files.copy(srcfile, destFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static byte[] load(String fileName) {
        File file = new File(fileName);
        if (!file.exists()) {
            return null;
        }

        byte[] content = new byte[(int)file.length()];
        try {
            FileInputStream in = new FileInputStream(file);
            in.read(content);
            in.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content;
    }

    private static volatile Boolean hasCreatedRandomKey = false;
    private static String randomKeyFileName = "random-key";
    public static byte[] loadRandomKey(String path) {
        byte[] aesKey;
        if (!hasCreatedRandomKey) {
            synchronized (hasCreatedRandomKey) {
                BtRandom secureRandom = new BtRandom();
                aesKey = secureRandom.nextBytes(16);
                save(Converter.byteArrayToHexString(aesKey), path + randomKeyFileName);
                hasCreatedRandomKey = true;
            }
        }

        File file = new File(path + randomKeyFileName);
        if (file.exists()) {
            byte[] hexBytes = load(path + randomKeyFileName);
            return Converter.hexStringToByteArray(new String(hexBytes));
        }

        BtRandom secureRandom = new BtRandom();
        aesKey = secureRandom.nextBytes(16);
        save(Converter.byteArrayToHexString(aesKey), path + randomKeyFileName);
        return aesKey;
    }
}
