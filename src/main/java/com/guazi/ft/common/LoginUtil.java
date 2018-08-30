package com.guazi.ft.common;

import com.guazi.ft.constant.LoginConstant;
import com.guazi.ft.dao.consign.model.UserDO;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.redis.base.ValueOperationsCache;
import com.guazi.ft.rest.RestResult;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

/**
 * 登录工具类
 *
 * @author shichunyang
 */
@Slf4j
public class LoginUtil {

	/**
	 * 获取登录用户信息
	 *
	 * @param request 请求对象
	 * @return 用户信息
	 */
	public static UserDO getLoginUser(HttpServletRequest request) {
		ValueOperationsCache valueOperationsCache = SpringContextUtil.getBean(ValueOperationsCache.class);

		String loginToken = getToken(request);
		if (StringUtil.isNull(loginToken)) {
			log.info("url==>{}, login_token 不存在", request.getRequestURL());
			throw new FtException(RestResult.ERROR_SIGN_CODE, "签名错误");
		}

		String redisTokenKey = StringUtil.append(StringUtil.REDIS_SPLIT, LoginConstant.REDIS_LOGIN_TOKEN, loginToken);
		String userJson = valueOperationsCache.get(redisTokenKey);

		if (StringUtil.isNull(userJson)) {
			log.info("url==>{}, login_token 不存在或已过期", request.getRequestURL());
			throw new FtException(RestResult.ERROR_SIGN_CODE, "签名错误");
		}

		return JsonUtil.json2Object(userJson, UserDO.class);
	}

	/**
	 * 从请求或cookie中获取token
	 *
	 * @param request 请求对象
	 * @return token
	 */
	public static String getToken(HttpServletRequest request) {
		// 从请求参数中获取
		String loginToken = request.getParameter(LoginConstant.PARAM_LOGIN_TOKEN);
		if (!StringUtil.isEmpty(loginToken)) {
			return loginToken;
		}

		// 从request域中获取
		loginToken = (String) request.getAttribute(LoginConstant.PARAM_LOGIN_TOKEN);
		if (!StringUtil.isEmpty(loginToken)) {
			return loginToken;
		}

		// 从header中获取
		loginToken = request.getHeader(LoginConstant.PARAM_LOGIN_TOKEN);
		if (!StringUtil.isEmpty(loginToken)) {
			return loginToken;
		}

		// 从cookie中获取
		loginToken = CookieUtil.getCookie(request, LoginConstant.PARAM_LOGIN_TOKEN);

		return loginToken;
	}
}
