package com.rst.pkm.dto.response;

import com.rst.pkm.dto.Readable;
import lombok.Data;

/**
 * @author hujia
 */
@Data
public class ResGenerateService extends Readable {
    private String serviceId;
    private String privateKey;
}
