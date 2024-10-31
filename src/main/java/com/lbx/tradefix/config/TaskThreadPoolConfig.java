package com.lbx.tradefix.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author liuyaohui 00032384
 * @date 2022/7/4 17:42
 */
@Configuration
public class TaskThreadPoolConfig {
    public static final String EXPORT_EXECUTOR_BEAN_NAME = "customPoolExecutor";

    @Value("${task.pool.coreSize: 10}")
    private int coreSize;

    @Value("${task.pool.maxSize: 20}")
    private int maxSize;

    @Value("${task.pool.keepAlive: 300}")
    private int keepAlive;

    @Value("${task.pool.queueCapacity: 15000}")
    private int queueCapacity;

    @Value("${task.pool.awaitTermination: 60}")
    private int awaitTermination;

    @Value("${task.pool.threadNamePrefix: task-pool-}")
    private String threadNamePrefix;

    @Bean(EXPORT_EXECUTOR_BEAN_NAME)
    public Executor customPoolExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(coreSize);
        // 最大线程数
        executor.setMaxPoolSize(maxSize);
        // 任务队列的大小
        executor.setQueueCapacity(queueCapacity);
        // 线程池名的前缀
        executor.setThreadNamePrefix(threadNamePrefix);
        // 允许线程的空闲时间 秒
        executor.setKeepAliveSeconds(keepAlive);
        // 设置线程池关闭的时候等待所有任务都完成再继续销毁其他的Bean
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // 设置线程池中任务的等待时间，如果超过这个时候还没有销毁就强制销毁，以确保应用最后能够被关闭，而不是阻塞住
        executor.setAwaitTerminationSeconds(awaitTermination);

        /**
         * 拒绝处理策略
         * CallerRunsPolicy()：交由调用方线程运行，比如 main 线程。
         * AbortPolicy()：直接抛出异常。
         * DiscardPolicy()：直接丢弃。
         * DiscardOldestPolicy()：丢弃队列中最老的任务。
         */
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        // 线程初始化
        executor.initialize();
        return executor;
    }
}
