package com.guazi.ft.cloud;

import org.springframework.stereotype.Component;

/**
 * FeignRequestInterceptor
 *
 * @author shichunyang
 */
@Component
public class FeignRequestInterceptor /*implements RequestInterceptor*/ {

//    @Override
//    public void apply(RequestTemplate requestTemplate) {
//        String xRequestId = LogAspect.REQUEST_HEADER.get();
//        requestTemplate.header(LogAspect.REQUEST_ID, xRequestId);
//        LogAspect.REQUEST_HEADER.remove();
//    }
}
