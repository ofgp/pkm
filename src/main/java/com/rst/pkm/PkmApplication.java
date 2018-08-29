package com.rst.pkm;

import com.rst.pkm.common.AESUtil;
import com.rst.pkm.common.Constant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.StringUtils;

/**
 * @author hujia
 */
@SpringBootApplication
public class PkmApplication {
	private static final Logger logger = LoggerFactory.getLogger(PkmApplication.class);

	public static void main(String[] args) {
	    String password = null;
	    if (args != null) {
            for (int i = 0; i < args.length; i++) {
                if (args[i].startsWith("--password=")) {
                    password = args[i].substring("--password=".length(), args[i].length());
                    break;
                } else if (args[i].startsWith("-password=")) {
                    password = args[i].substring("-password=".length(), args[i].length());
                    break;
                }
            }
        }

		if (StringUtils.isEmpty(password)) {
			logger.error("You must startup with your keystore password!");
			System.exit(-1);
		} else {
	        //for load encrypted data
            Constant.ADMIN_KEY = AESUtil.aesKeyFrom(password);
        }

		SpringApplication.run(PkmApplication.class, args);
	}
}
