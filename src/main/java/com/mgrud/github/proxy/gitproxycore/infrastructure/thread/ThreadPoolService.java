package com.mgrud.github.proxy.gitproxycore.infrastructure.thread;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.*;

@Service
public class ThreadPoolService {

    private ExecutorService executorService;


    public ExecutorService getExecutorService() {
        if (Objects.isNull(executorService)) {
            initExecutorService();
        }
        return executorService;
    }

    private void initExecutorService() {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("github-proxy-core-thread-%d")
                .build();

        executorService = new ThreadPoolExecutor(5, 10, 60l, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), threadFactory);
    }

    @PreDestroy
    void clean() {
        if (Objects.nonNull(executorService)) {
            executorService.shutdown();
        }
    }
}
