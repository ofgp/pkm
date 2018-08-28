package com.rst.pkm.common;

import com.google.common.io.Files;

import java.io.*;

/**
 * @author hujia
 */
public class FileUtil {
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

    public static boolean backup(String fileName, String backupName) {
        File srcfile = new File(fileName);
        File destFile = new File(backupName);
        if (!destFile.getParentFile().exists()) {
            destFile.getParentFile().mkdirs();
        }

        if (destFile.exists()) {
            destFile.delete();
        }

        if (!srcfile.exists()) {
            return false;
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
