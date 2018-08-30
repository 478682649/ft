package com.guazi.ft.common;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.dozer.DozerBeanMapper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 常用工具类
 *
 * @author shichunyang
 */
public class CommonUtil {

    /**
     * 系统换行符
     */
    public static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * 打印请求参数
     *
     * @param request 请求对象
     * @return 参数json格式
     */
    public static String getParamsFromRequest(HttpServletRequest request) {
        String params = "";

        Map<String, String[]> requestParamMap = request.getParameterMap();
        if (!requestParamMap.isEmpty()) {
            params = JsonUtil.object2Json(requestParamMap);
        }

        return params;
    }

    /**
     * 获取一个32位的UUID码
     *
     * @return 32位UUID
     */
    public static String get32UUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 获取一个36位的UUID码
     *
     * @return 36位UUID
     */
    public static String get36UUID() {
        return UUID.randomUUID().toString();
    }

    /**
     * URL编码
     *
     * @param str 要编码的字符串
     * @return 编码后的字符串
     */
    public static String encode(String str) {
        try {
            return URLEncoder.encode(str, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * URL解码
     *
     * @param str 要解码的字符串
     * @return 解码后的字符串
     */
    public static String decode(String str) {
        try {
            return URLDecoder.decode(str, "utf-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * MD5加密
     *
     * @param data 需要加密的字符串
     * @return 加密后的字符串
     */
    public static String md5Hex(String data) {
        return DigestUtils.md5Hex(data);
    }

    /**
     * base64 编码
     *
     * @param str 要编码的字符串
     * @return 编码后的字符串
     */
    public static String base64Encode(String str) {
        String result = null;
        try {
            result = Base64.encodeBase64String(str.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * base64 解码
     *
     * @param str 要解码的字符串
     * @return 解码后的字符串
     */
    public static String base64Decode(String str) {
        String result = null;
        try {
            result = new String(Base64.decodeBase64(str), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 生成指定范围内不重复的N个数字
     *
     * @param min    最小值
     * @param max    最大值
     * @param number 返回个数
     */
    public static List<Integer> randomList(int min, int max, int number) {
        List<Integer> numberList = new ArrayList<>();

        if (number > (max - min + 1) || max < min) {
            return numberList;
        }

        int count = 1;

        while (count <= number) {
            int num = (int) (Math.random() * (max - min + 1)) + min;

            if (!numberList.contains(num)) {
                numberList.add(num);
                count++;
            }
        }

        return numberList;
    }

    /**
     * 根据当前时间生成22位的日期随机数(不支持并发访问)
     *
     * @return 返回日期随机数
     */
    public synchronized static String getOrderNumber() {
        String currentDate = new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date());
        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return currentDate;
    }

    /**
     * 从资源文件中获取Properties对象
     *
     * @param file 资源文件名称
     * @return Properties对象
     */
    public static Properties getProperties(String file) {
        Properties properties = new Properties();

        InputStream in = null;

        try {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(file);
            properties.load(in);
        } catch (Exception e) {
            e.printStackTrace();
            properties = null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return properties;
    }

    /**
     * 格式化字符串
     *
     * @param template 字符串模板 "{0}{1}{2}{3}{4}{5}{6}{7}{8}"
     * @param args     格式化参数
     * @return 格式化后的字符串
     */
    public static String formatStr(String template, Object... args) {
        return MessageFormat.format(template, args);
    }

    /**
     * 把Map转换成指定类型JavaBean
     *
     * @param map   (map的键就是javaBean的属性名)
     * @param clazz (要生成javaBean的class对象)
     * @return javaBean
     */
    @SuppressWarnings("unchecked")
    public static <T> T map2Bean(Map<String, ?> map, Class<T> clazz, String datePattern) {
        try {
            T bean = clazz.getConstructor().newInstance();

            ConvertUtils.register(new Converter() {
                @Override
                public Date convert(Class type, Object value) {
                    if (value == null) {
                        return null;
                    }

                    if (value instanceof String && !StringUtil.isNull((String) value)) {
                        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
                        try {
                            return sdf.parse((String) value);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (value instanceof Date) {
                        return (Date) value;
                    }

                    return null;
                }
            }, java.util.Date.class);

            BeanUtils.populate(bean, map);

            return bean;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 处理http编码问题
     */
    public static void httpCode(HttpServletRequest request, HttpServletResponse response, String contentType) throws IOException {

        // 解决请求体乱码问题
        request.setCharacterEncoding("utf-8");

        if (contentType == null || contentType.trim().isEmpty()) {
            contentType = "text/html;charset=utf-8";
        }

        response.setContentType(contentType);
    }

    public static <T> T exchange(Object sourceObject, Class<T> clazz) {
        return new DozerBeanMapper().map(sourceObject, clazz);
    }

    /**
     * 判断类或方法上是否有指定的注解类型
     *
     * @param beanType        类对象
     * @param method          方法对象
     * @param annotationClass 注解对象
     * @return true:包含
     */
    @SuppressWarnings("unchecked")
    public static boolean isAnnotationPresent(
            Class beanType,
            Method method,
            Class<? extends Annotation> annotationClass
    ) {
        boolean check = false;

        if (beanType != null) {
            check = beanType.isAnnotationPresent(annotationClass);
        }
        if (method != null) {
            check = (check || method.isAnnotationPresent(annotationClass));
        }
        return check;
    }

    /**
     * 比较两个值是否相等
     *
     * @param a 值a
     * @param b 值b
     * @return true:相等
     */
    public static boolean equals(Object a, Object b) {
        return Objects.equals(a, b);
    }

    /**
     * 递归删除
     *
     * @param srcFolder 文件夹
     */
    public static void deleteDir(File srcFolder) {
        File[] files = srcFolder.listFiles();
        if (files != null) {
            // 删除内层文件
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDir(file);
                } else {
                    boolean flag = file.delete();
                    System.out.println("删除内层文件==>" + file.getName() + ", result==>" + flag);
                }
            }

            // 删完文件夹中内容后 将文件夹删掉
            boolean flag = srcFolder.delete();

            System.out.println("删除外层文件夹==>" + srcFolder.getName() + ", result==>" + flag);
        }
    }

    /**
     * 转换map参数
     */
    public static String exchangeParams(Map<String, String> params) {
        String result = params.entrySet().stream().reduce("", (temporary, entry) -> temporary.concat(entry.getKey()).concat("=").concat(StringUtil.isNull(entry.getValue()) ? "" : entry.getValue().trim()).concat("&"), String::concat);
        return result.substring(0, result.length() - 1);
    }

    public static void main(String[] args) {
    }
}
