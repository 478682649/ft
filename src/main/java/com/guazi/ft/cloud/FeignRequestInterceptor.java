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
//        String xRequestId = ControllerAspect.REQUEST_HEADER.get();
//        requestTemplate.header(ControllerAspect.REQUEST_ID, xRequestId);
//        ControllerAspect.REQUEST_HEADER.remove();
//    }
}
