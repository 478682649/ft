package com.guazi.ft.common.thread;

import com.guazi.ft.common.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpThreadUtil
 *
 * @author shichunyang
 */
@Slf4j
public class HttpThreadUtil {

    /**
     * 批量请求
     *
     * @param httpParams      请求参数
     * @param executorService 线程池
     * @return 批量结果
     */
    public static List<String> batchHttp(
            String taskName,
            List<HttpParam> httpParams,
            ExecutorService executorService
    ) {
        CountDownLatch countDownLatch = new CountDownLatch(httpParams.size());
        AtomicInteger atomicInteger = new AtomicInteger();
        Map<Integer, String> resultMap = new ConcurrentHashMap<>(16);

        log.info("taskName==>{}, 任务总数==>{}", taskName, countDownLatch.getCount());

        httpParams
                .parallelStream()
                .map(httpParam -> (Runnable) () -> {
                    try {
                        String result = HttpUtil.httpRequest(
                                httpParam.getUrl(),
                                httpParam.getMethod(),
                                httpParam.getData(),
                                httpParam.getResponseCharset(),
                                httpParam.getCookie(),
                                httpParam.getContentType()
                        );
                        resultMap.put(atomicInteger.getAndIncrement(), result);
                    } catch (Exception e) {
                        resultMap.put(atomicInteger.getAndIncrement(), e.getMessage());
                    } finally {
                        countDownLatch.countDown();
                    }
                })
                .forEach(executorService::submit);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new ArrayList<>(resultMap.values());
    }
}
