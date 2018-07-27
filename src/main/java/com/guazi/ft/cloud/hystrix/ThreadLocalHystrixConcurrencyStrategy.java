package com.guazi.ft.cloud.hystrix;

/**
 * ThreadLocalHystrixConcurrencyStrategy
 *
 * @author shichunyang
 */
/*
@Slf4j
public class ThreadLocalHystrixConcurrencyStrategy extends HystrixConcurrencyStrategy {
    @Override
    public ThreadPoolExecutor getThreadPool(HystrixThreadPoolKey threadPoolKey, HystrixThreadPoolProperties threadPoolProperties) {

        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("hystrix-" + threadPoolKey.name() + "-pool-%d").build();

        log.info("{}, coreSize==>{}", threadPoolKey.name(), threadPoolProperties.coreSize().get());
        log.info("{}, actualMaximumSize==>{}", threadPoolKey.name(), threadPoolProperties.actualMaximumSize());
        log.info("{}, keepAliveTimeMinutes==>{}", threadPoolKey.name(), threadPoolProperties.keepAliveTimeMinutes().get());
        log.info("{}, maxQueueSize==>{}", threadPoolKey.name(), threadPoolProperties.maxQueueSize().get());

        return new ThreadLocalThreadPoolExecutor(
                threadPoolProperties.coreSize().get(),
                threadPoolProperties.actualMaximumSize(),
                threadPoolProperties.keepAliveTimeMinutes().get(),
                TimeUnit.MINUTES,
                super.getBlockingQueue(threadPoolProperties.maxQueueSize().get()),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
*/
