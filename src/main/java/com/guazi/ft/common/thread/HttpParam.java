package com.guazi.ft.common.thread;

import lombok.Data;

/**
 * 请求参数
 *
 * @author shichunyang
 */
@Data
public class HttpParam {
    private String url;
    private String method;
    private String data;
    private String responseCharset;
    private String cookie;
    private String contentType;
}
