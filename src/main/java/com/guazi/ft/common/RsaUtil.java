package com.guazi.ft.common;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * RSA算法工具类
 *
 * @author shichunyang
 */
public class RsaUtil {
	/**
	 * 签名类型
	 */
	public static final String SIGN_TYPE = "RSA";

	/**
	 * 签名算法
	 */
	public static final String SIGN_ALGORITHMS = "SHA1WithRSA";

	/**
	 * 编码
	 */
	private static final String DEFAULT_CHARSET = "utf-8";

	/**
	 * 获取公钥的key
	 */
	public static final String PUBLIC_KEY = "RSAPublicKey";

	/**
	 * 获取私钥的key
	 */
	public static final String PRIVATE_KEY = "RSAPrivateKey";

	/**
	 * 签名参数名称
	 */
	public static final String SIGNATURE_NAME = "signature";

	/**
	 * RSA签名
	 *
	 * @param data       待签名数据
	 * @param privateKey 私钥
	 * @return 签名值
	 */
	public static String getSign(String data, String privateKey) {
		try {
			PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey));
			KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE);
			PrivateKey generatePrivate = keyFactory.generatePrivate(pkcs8EncodedKeySpec);

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
			signature.initSign(generatePrivate);
			signature.update(data.getBytes(DEFAULT_CHARSET));

			byte[] signByteArr = signature.sign();
			return Base64.getEncoder().encodeToString(signByteArr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * RSA验签名检查
	 *
	 * @param data      待签名数据
	 * @param sign      签名值
	 * @param publicKey 公钥
	 * @return 布尔值 返回true  签名验证成功  否则签名失败
	 */
	public static boolean checkSign(String data, String sign, String publicKey) {
		try {
			KeyFactory keyFactory = KeyFactory.getInstance(SIGN_TYPE);

			byte[] decodePublicKey = Base64.getDecoder().decode(publicKey);
			PublicKey pubKey = keyFactory.generatePublic(new X509EncodedKeySpec(decodePublicKey));

			Signature signature = Signature.getInstance(SIGN_ALGORITHMS);
			signature.initVerify(pubKey);
			signature.update(data.getBytes(DEFAULT_CHARSET));

			return signature.verify(Base64.getDecoder().decode(sign));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 生成密钥对(公钥和私钥)
	 */
	public static Map<String, Object> getKeyMap() throws Exception {
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SIGN_TYPE);
		keyPairGenerator.initialize(2048);

		KeyPair keyPair = keyPairGenerator.generateKeyPair();
		RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
		RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

		Map<String, Object> keyMap = new HashMap<>(16);

		keyMap.put(PUBLIC_KEY, publicKey);
		keyMap.put(PRIVATE_KEY, privateKey);

		return keyMap;
	}

	/**
	 * 获取私钥
	 *
	 * @param keyMap 密钥对
	 */
	public static String getPrivateKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PRIVATE_KEY);

		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * 获取公钥
	 *
	 * @param keyMap 密钥对
	 */
	public static String getPublicKey(Map<String, Object> keyMap) throws Exception {
		Key key = (Key) keyMap.get(PUBLIC_KEY);

		return Base64.getEncoder().encodeToString(key.getEncoded());
	}

	/**
	 * 获取用于签名的参数集合
	 *
	 * @param request 请求对象
	 * @return 用于签名的参数集合
	 */
	public static SortedMap<String, String> getSignatureParams(HttpServletRequest request, String signatureName) {
		SortedMap<String, String> signatureParams = new TreeMap<>();

		Enumeration<String> requestParameterNames = request.getParameterNames();
		while (requestParameterNames.hasMoreElements()) {
			String requestParameterName = requestParameterNames.nextElement();
			if (StringUtils.equals(requestParameterName, signatureName)) {
				continue;
			}
			signatureParams.put(requestParameterName, request.getParameter(requestParameterName));
		}

		signatureParams.entrySet().forEach(entry -> {
			try {
				entry.setValue(URLEncoder.encode(entry.getValue(), HttpUtil.DEFAULT_RESPONSE_CHARSET));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		});

		return signatureParams;
	}

	public static void main(String[] args) throws Exception {
		Map<String, Object> keyMap = RsaUtil.getKeyMap();

		String privateKey = RsaUtil.getPrivateKey(keyMap);
		String publicKey = RsaUtil.getPublicKey(keyMap);

		System.out.println(privateKey);
		System.out.println(publicKey);

		String sign = RsaUtil.getSign("密文Ab123456*&.,", privateKey);

		System.out.println(RsaUtil.checkSign("密文Ab123456*&.,", sign, publicKey));
	}
}
