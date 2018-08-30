package com.guazi.ft.cloud.hystrix;

import com.alibaba.ttl.TtlRunnable;

import java.util.concurrent.*;

/**
 * ThreadLocalThreadPoolExecutor
 *
 * @author shichunyang
 */
public class ThreadLocalThreadPoolExecutor extends ThreadPoolExecutor {
	@Override
	public void execute(Runnable command) {
		super.execute(TtlRunnable.get(command));
	}

	public ThreadLocalThreadPoolExecutor(int corePoolSize,
										 int maximumPoolSize,
										 long keepAliveTime,
										 TimeUnit unit,
										 BlockingQueue<Runnable> workQueue,
										 ThreadFactory threadFactory,
										 RejectedExecutionHandler handler) {
		super(corePoolSize,
				maximumPoolSize,
				keepAliveTime,
				unit,
				workQueue,
				threadFactory,
				handler);
	}
}
