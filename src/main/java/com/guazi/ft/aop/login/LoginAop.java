package com.guazi.ft.aop.login;

import com.guazi.ft.common.HttpUtil;
import com.guazi.ft.common.JsonUtil;
import com.guazi.ft.common.LoginUtil;
import com.guazi.ft.common.StringUtil;
import com.guazi.ft.dao.consign.model.UserDO;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录校验token
 *
 * @author shichunyang
 */
@Aspect
@Slf4j
public class LoginAop {

    @Before(value = "@annotation(com.guazi.ft.aop.login.LoginCheck)")
    public void before() {

        // 获取request对象
        ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String ip = HttpUtil.getIpAddress(request);

        String referer = request.getHeader("REFERER");
        String target = "swagger-ui.html";
        if (!StringUtil.isNull(referer) && referer.contains(target)) {
            log.info("ip==>{}, url==>{}, swagger request", ip, request.getRequestURL());
        }

        UserDO userDO = LoginUtil.getLoginUser(request);

        log.info("ip==>{}, url==>{}, login_token 验证通过, user==>{}", ip, request.getRequestURL(), JsonUtil.object2Json(userDO));
    }
}
