package com.guazi.ft.common.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConfigParam {
    /**
     * @return 参数名称
     */
    String paramName();

    /**
     * @return 参数值
     */
    String paramValue();
}
