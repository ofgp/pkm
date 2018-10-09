package com.rst.pkm.controller.interceptor;

import com.rst.pkm.common.Constant;
import com.rst.pkm.common.Converter;
import com.rst.pkm.common.Error;
import com.rst.pkm.config.CurrentThreadData;
import com.rst.pkm.data.entity.ServiceProfile;
import com.rst.pkm.data.dao.ServiceProfileDao;
import com.rst.pkm.dto.request.BaseRequest;
import com.rst.pkm.service.KeyService;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdviceAdapter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 *
 * @author hujia
 * @date 2017/6/21
 */
@ControllerAdvice(basePackages = "com.rst1.pkm.controller")
public class RequestBodyAdvice extends RequestBodyAdviceAdapter {
    public static final int MAX_LIVE_SECOND = 300;

    private final Logger logger = LoggerFactory.getLogger(RequestBodyAdvice.class);

    @Autowired
    private KeyService keyService;
    @Autowired
    private ServiceProfileDao spRepository;

    @Override
    public boolean supports(MethodParameter methodParameter,
                            Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return !methodParameter.getContainingClass().isAnnotationPresent(DisableSignatureCheck.class)
                && !methodParameter.getMethod().isAnnotationPresent(DisableSignatureCheck.class);
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage,
                                           MethodParameter parameter,
                                           Type targetType,
                                           Class<? extends HttpMessageConverter<?>> converterType) throws IOException {
        return new HttpInputMessage() {

            @Override
            public InputStream getBody() throws IOException {
                String serviceId = inputMessage.getHeaders().getFirst(Constant.SERVICE_ID);
                if (StringUtils.isEmpty(serviceId)) {
                    CustomException.response(Error.SID_NOT_PRESENT);
                }

                CurrentThreadData.setServiceId(serviceId);

                String signature = inputMessage.getHeaders().getFirst(Constant.SIGNATURE_HEADER);
                if (StringUtils.isEmpty(signature) || StringUtils.isEmpty(serviceId)) {
                    CustomException.response(Error.SIGNATURE_NOT_PRESENT);
                }

                CurrentThreadData.setSignature(signature);

                ServiceProfile sp = spRepository.findByServiceId(serviceId);
                if (sp == null) {
                    CustomException.response(Error.SID_INVALID);
                }

                String bodyString = StreamUtils.copyToString(inputMessage.getBody(), Constant.DEFAULT_CHARSET);
                logger.info("【Request】signature:{}, body:{}", signature, bodyString);
                BaseRequest br = new Gson().fromJson(bodyString, BaseRequest.class);
                br.setServiceId(serviceId);

                if (System.currentTimeMillis() / 1000 - br.getTimestamp() > MAX_LIVE_SECOND) {
                    CustomException.response(Error.REQUEST_EXPIRED);
                }

                byte[] msgHash = Converter.sha256(bodyString.getBytes(Constant.DEFAULT_CHARSET));

                boolean ret = keyService.verify(msgHash,
                        Converter.hexStringToByteArray(sp.getPublicKey()),
                        Converter.hexStringToByteArray(signature));

                if (!ret) {
                    CustomException.response(Error.SIGNATURE_INVALID);
                }

                return new ByteArrayInputStream(bodyString.getBytes());
            }

            @Override
            public HttpHeaders getHeaders() {
                return inputMessage.getHeaders();
            }
        };
    }
}
