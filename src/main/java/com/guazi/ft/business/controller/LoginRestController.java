package com.guazi.ft.business.controller;

import com.guazi.ft.aop.login.LoginCheck;
import com.guazi.ft.common.*;
import com.guazi.ft.constant.LoginConstant;
import com.guazi.ft.dao.consign.model.po.UserPO;
import com.guazi.ft.exception.FtException;
import com.guazi.ft.redis.base.ValueOperationsCache;
import com.guazi.ft.rest.RestResult;
import com.guazi.ft.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * 登录相关api
 *
 * @author shichunyang
 */
@Api(tags = "登陆API")
@RestController
@Slf4j
@CrossOrigin
@RequestMapping(RestResult.API)
public class LoginRestController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private ValueOperationsCache valueOperationsCache;

	@Value("${cookieDomain}")
	private String cookieDomain;

	@ApiOperation("登录")
	/**
	 * 登录
	 *
	 * @param username 用户名
	 * @param password 密码
	 * @param code     验证码
	 * @return token
	 */
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public String login(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "code") String code,
			@RequestParam(value = "code_id") String codeId
	) {
		username = username.trim();
		password = password.trim();
		code = code.trim();
		codeId = codeId.trim();

		String token = loginService.getLoginToken(username, password, code, codeId);

		request.setAttribute(LoginConstant.PARAM_LOGIN_TOKEN, token);

		Map<String, Object> result = new HashMap<>(16);
		result.put(LoginConstant.RETURN_TOKEN, token);
		result.put(LoginConstant.PARAM_LOGIN_USER, LoginUtil.getLoginUser(request));

		String domain = this.cookieDomain;
		CookieUtil.addCookie(response, LoginConstant.PARAM_LOGIN_TOKEN, token, CookieUtil.MAX_AGE_BROWSER, domain, true);

		return JsonUtil.object2Json(RestResult.getSuccessRestResult(result));
	}

	@ApiOperation("图片验证码")
	/**
	 * 图片验证码
	 */
	@RequestMapping(value = "/code", method = RequestMethod.POST)
	public String code() {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		String code = ImageUtil.getImage(byteArrayOutputStream);
		String codeId = CommonUtil.get32UUID();

		String codeRedisKey = StringUtil.append(StringUtil.REDIS_SPLIT, LoginConstant.REDIS_VERIFICATION_CODE, codeId);
		boolean flag = valueOperationsCache.setNX(codeRedisKey, code, 300_000L);
		if (!flag) {
			throw new FtException(RestResult.ERROR_CODE, "验证码存储异常");
		}

		Map<String, Object> result = new HashMap<>(16);
		result.put("code_id", codeId);
		result.put("img", Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray()));
		return JsonUtil.object2Json(RestResult.getSuccessRestResult(result));
	}

	@ApiOperation("查询当前登录用户信息")
	/**
	 * 查询当前登录用户信息
	 *
	 * @return 当前登录用户信息
	 */
	@RequestMapping(value = "/user", method = RequestMethod.POST)
	@LoginCheck
	public String user(HttpServletRequest request) {
		return JsonUtil.object2Json(RestResult.getSuccessRestResult(LoginUtil.getLoginUser(request)));
	}

	@RequestMapping(value = "/json-param", method = RequestMethod.POST)
	@LoginCheck
	public String jsonParam(@RequestBody UserPO body) {
		return JsonUtil.object2Json(body);
	}
}
