package com.guazi.ft.common;

import com.guazi.ft.aop.ControllerAspect;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Http工具类
 *
 * @author shichunyang
 */
@Slf4j
public class HttpUtil {
	/**
	 * 代理是否开启
	 */
	public static final boolean PROXY_FLAG = false;
	/**
	 * 代理主机
	 */
	public static final String PROXY_HOST = "10.30.144.2";
	/**
	 * 代理端口
	 */
	public static final int PROXY_PORT = 9999;
	/**
	 * 默认响应编码
	 */
	public static final String DEFAULT_RESPONSE_CHARSET = "UTF-8";

	/**
	 * 连接超时(毫秒)
	 */
	public static final int CONNECT_TIMEOUT = 30_000;
	/**
	 * 读取超时(毫秒)
	 */
	public static final int READ_TIMEOUT = 30_000;

	/**
	 * GET请求方式
	 */
	public static final String GET = "GET";
	/**
	 * POST请求方式
	 */
	public static final String POST = "POST";

	public static final String CONTENT_TYPE_TEXT = "application/x-www-form-urlencoded";
	public static final String CONTENT_TYPE_JSON = "application/json; charset=UTF-8";

	/**
	 * Post请求
	 */
	public static String post(String requestUrl, Map<String, String> paramMap) {
		return HttpUtil.httpRequest(requestUrl, POST, CommonUtil.exchangeParams(paramMap), DEFAULT_RESPONSE_CHARSET, null, CONTENT_TYPE_TEXT);
	}

	/**
	 * GET请求
	 */
	public static String get(String requestUrl, Map<String, String> paramMap) {
		return HttpUtil.httpRequest(requestUrl + "?" + CommonUtil.exchangeParams(paramMap), GET, null, DEFAULT_RESPONSE_CHARSET, null, CONTENT_TYPE_TEXT);
	}

	/**
	 * HTTP请求
	 *
	 * @param requestUrl    请求路径
	 * @param requestMethod 请求方式(GET  POST)
	 * @param data          POST请求数据(中文需要URL编码),GET请求data传递NULL
	 * @return 服务器返回信息(字符串)
	 */
	public static String httpRequest(
			String requestUrl,
			String requestMethod,
			String data,
			String charset,
			String sessionId,
			String contentType
	) {
		long start = System.currentTimeMillis();

		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36";

		OutputStream out = null;
		HttpURLConnection connection = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(requestUrl);

			if (PROXY_FLAG) {
				//开启代理
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			connection.setRequestMethod(requestMethod);

			if (sessionId != null && !sessionId.trim().isEmpty()) {
				connection.setRequestProperty("Cookie", sessionId);
			}

			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);

			connection.setRequestProperty("User-agent", userAgent);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Accept", "*/*");
			connection.setRequestProperty("Content-Type", contentType);
			connection.setRequestProperty(ControllerAspect.REQUEST_ID, ControllerAspect.REQUEST_HEADER.get());

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);

			if (null != data) {
				connection.setFixedLengthStreamingMode(data.getBytes().length);
			}

			//获取请求头
			Map<String, List<String>> requestHeaderMap = connection.getRequestProperties();
			log.info("http==>{}, 请求头==>{}", requestUrl, JsonUtil.object2Json(requestHeaderMap));

			connection.connect();

			if (null != data) {
				out = connection.getOutputStream();
				out.write(data.getBytes("UTF-8"));
			}

			Map<String, List<String>> responseHeaderMap = new HashMap<>(connection.getHeaderFields());
			responseHeaderMap.remove(null);
			log.info("http==>{}, 响应头==>{}", requestUrl, JsonUtil.object2Json(responseHeaderMap));

			String code = connection.getResponseCode() + "";

			String successStart = "2";
			String turnStart = "3";

			StringBuilder sb = new StringBuilder();
			String resultLine;
			if (code.startsWith(successStart) || code.startsWith(turnStart)) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
				while ((resultLine = reader.readLine()) != null) {
					sb.append(resultLine);
					sb.append(CommonUtil.LINE_SEPARATOR);
				}

				String result = sb.toString().trim();

				long end = System.currentTimeMillis();

				if (code.startsWith(successStart)) {
					log.info("http==>{}, param==>{}, cost==>{}ms result==>{}", requestUrl, data, end - start, result);
					return result;
				} else {
					log.error("http==>{}, param==>{}, code==>{}, result==>{}", requestUrl, data, code, result);
					return result;
				}
			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), charset));
				while ((resultLine = reader.readLine()) != null) {
					sb.append(resultLine);
					sb.append(CommonUtil.LINE_SEPARATOR);
				}
				log.error("http==>{}, param==>{}, code==>{}, result==>{}", requestUrl, data, code, sb.toString().trim());
				return sb.toString().trim();
			}
		} catch (Exception e) {
			log.error("http==>{}, param==>{}, exception==>{}", requestUrl, data, JsonUtil.object2Json(e), e);
			return null;
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * 文件上传
	 *
	 * @param requestUrl  请求url
	 * @param textMap     文本Map
	 * @param fileMap     文件Map
	 * @param contentType 上传文件类型
	 * @param sessionId   sessionId
	 * @return 服务器响应内容
	 * ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	 * ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
	 */
	public static String upload(
			String requestUrl,
			Map<String, String> textMap,
			Map<String, Object[]> fileMap,
			String contentType,
			String charset,
			String sessionId
	) {
		long start = System.currentTimeMillis();

		// 定义上传表单分隔符
		String boundary = "----WebKitFormBoundaryvUCBTEK4dOTNvxM5";
		String userAgent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.146 Safari/537.36";
		// 换行符(多部件上传表单特有)
		String lineSeparator = "\r\n";

		HttpURLConnection connection = null;
		BufferedOutputStream out = null;
		BufferedReader reader = null;

		try {
			URL url = new URL(requestUrl);

			if (PROXY_FLAG) {
				// 开启代理
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(PROXY_HOST, PROXY_PORT));
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			connection.setRequestMethod(POST);

			if (sessionId != null && !sessionId.trim().isEmpty()) {
				connection.setRequestProperty("Cookie", sessionId);
			}

			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);

			connection.setRequestProperty("User-agent", userAgent);
			connection.setRequestProperty("Connection", "keep-alive");
			connection.setRequestProperty("Accept", "*/*");

			connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
			connection.setRequestProperty(ControllerAspect.REQUEST_ID, ControllerAspect.REQUEST_HEADER.get());

			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setUseCaches(false);
			connection.setInstanceFollowRedirects(true);

			connection.connect();

			out = new BufferedOutputStream(new DataOutputStream(connection.getOutputStream()));

			// 普通文本项
			if (textMap != null) {
				StringBuilder textSb = new StringBuilder();

				textMap.forEach((inputName, inputValue) -> {
					if (StringUtil.isNull(inputValue)) {
						inputValue = "";
					}

					textSb.append(lineSeparator).append("--").append(boundary).append(lineSeparator);

					textSb.append("Content-Disposition: form-data; name=\"").append(inputName).append("\"")
							.append(lineSeparator).append(lineSeparator);

					textSb.append(inputValue);
				});

				out.write(textSb.toString().getBytes());
			}

			// 文件文本项
			if (fileMap != null) {
				BufferedOutputStream finalOut = out;
				fileMap.forEach((inputName, fileNameAndInputStreamArr) -> {
					String filename = (String) fileNameAndInputStreamArr[0];

					String fileSb = lineSeparator + "--" + boundary + lineSeparator +
							"Content-Disposition: form-data; name=\"" + inputName + "\"; filename=\"" + filename +
							"\"" + lineSeparator +
							"Content-Type:" + contentType + lineSeparator + lineSeparator;

					try {
						finalOut.write(fileSb.getBytes());
					} catch (IOException e) {
						e.printStackTrace();
					}

					try (BufferedInputStream in = new BufferedInputStream((ByteArrayInputStream) fileNameAndInputStreamArr[1])) {
						int bytes;
						byte[] buffer = new byte[1024];
						while ((bytes = in.read(buffer)) != -1) {
							finalOut.write(buffer, 0, bytes);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				});
			}

			byte[] endData = (lineSeparator + "--" + boundary + "--" + lineSeparator).getBytes();

			out.write(endData);

			out.flush();

			String code = connection.getResponseCode() + "";

			String successStart = "2";
			String turnStart = "3";

			String resultLine;
			StringBuilder sb = new StringBuilder();

			if (code.startsWith(successStart) || code.startsWith(turnStart)) {
				reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
				while ((resultLine = reader.readLine()) != null) {
					sb.append(resultLine);
					sb.append(CommonUtil.LINE_SEPARATOR);
				}

				String result = sb.toString().trim();

				long end = System.currentTimeMillis();

				if (code.startsWith(successStart)) {
					// 返回字节长度
					long length = connection.getContentLengthLong();

					log.info("http==>{}, length==>{}, cost==>{}ms result==>{}", requestUrl, length, end - start, result);
					return result;
				} else {
					log.error("http==>{}, code==>{}, result==>{}", requestUrl, code, result);
					return result;
				}
			} else {
				reader = new BufferedReader(new InputStreamReader(connection.getErrorStream(), charset));
				while ((resultLine = reader.readLine()) != null) {
					sb.append(resultLine);
					sb.append(CommonUtil.LINE_SEPARATOR);
				}
				log.error("http==>{}, code==>{}, result==>{}", requestUrl, code, sb.toString().trim());
				return sb.toString().trim();
			}
		} catch (Exception e) {
			log.error("http==>{}, exception==>{}", requestUrl, JsonUtil.object2Json(e), e);
			return null;
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	/**
	 * 获取当前请求的所有请求头
	 *
	 * @param request 请求对象
	 * @return 请求头map
	 */
	public static Map<String, String> getHeaders(HttpServletRequest request) {

		Map<String, String> headerMap = new HashMap<>(16);

		Enumeration<String> headerNames = request.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = headerNames.nextElement();

			if (!StringUtil.isNull(headerName)) {
				headerMap.put(headerName.trim().toLowerCase(), request.getHeader(headerName));
			}
		}
		return headerMap;
	}

	/**
	 * 下载
	 *
	 * @param request  请求对象
	 * @param response 响应对象
	 * @param fileName 文件名
	 * @param in       输入流
	 * @throws Exception 下载出现异常
	 */
	public static void downLoad(HttpServletRequest request, HttpServletResponse response, String fileName, InputStream in) throws Exception {

		String mimeType = request.getServletContext().getMimeType(fileName);

		response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));

		// 设置mime类型
		response.setContentType(mimeType);

		IOUtils.copy(in, response.getOutputStream());
	}

	/**
	 * 获取客户端真实ip
	 */
	public static String getIpAddress(HttpServletRequest request) {

		String unknown = "unknown";
		String localhost1 = "127.0.0.1";
		String localhost2 = "0:0:0:0:0:0:0:1";

		int ipLength = 15;

		String ip = request.getHeader("X-Forwarded-For");

		if (StringUtil.isNull(ip) || unknown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}

		if (StringUtil.isNull(ip) || unknown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}

		if (StringUtil.isNull(ip) || unknown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}

		if (StringUtil.isNull(ip) || unknown.equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}

		if (StringUtil.isNull(ip) || unknown.equalsIgnoreCase(ip)) {

			ip = request.getRemoteAddr();
			if (ip.equals(localhost1) || ip.equals(localhost2)) {

				// 根据网卡取本机配置的IP
				InetAddress inetAddress;
				try {
					inetAddress = InetAddress.getLocalHost();
					ip = inetAddress.getHostAddress();
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
		}

		if (!StringUtil.isNull(ip) && ip.length() > ipLength) {
			String[] ips = ip.split(",");

			for (String ipStr : ips) {
				if (!unknown.equalsIgnoreCase(ipStr)) {
					ip = ipStr;
					break;
				}
			}
		}

		return ip;
	}

	public static void main(String[] args) throws Exception {
		String url = "http://localhost:9001/upload";
		Map<String, String> textMap = new HashMap<String, String>(16) {
			{
				put("username", "史春阳");
			}
		};
		Map<String, Object[]> fileMap = new HashMap<String, Object[]>(16) {
			{
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				IOUtils.copy(new FileInputStream("/Users/shichunyang/person.xls"), byteArrayOutputStream);
				Object[] args = {"史春阳excel.xls", new ByteArrayInputStream(byteArrayOutputStream.toByteArray())};
				put("excel", args);
			}
		};
		System.out.println(upload(url, textMap, fileMap, null, DEFAULT_RESPONSE_CHARSET, null));
	}
}
