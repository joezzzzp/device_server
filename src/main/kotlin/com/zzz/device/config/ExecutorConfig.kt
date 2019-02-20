package com.zzz.device.config

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger


/**
@author zzz
@date 2019/2/20 10:11
 **/

@Configuration
class ExecutorConfig {

    companion object {
        private val THREAD_POOL_EXECUTOR_CORE_POOL_SIZE = Runtime.getRuntime().availableProcessors()
        private val THREAD_POOL_EXECUTOR_MAX_POOL_SIZE = THREAD_POOL_EXECUTOR_CORE_POOL_SIZE * 2
    }

    @Bean("syncTaskExecutor")
    fun syncTaskExecutor(): ExecutorService =
            ThreadPoolExecutor(
                THREAD_POOL_EXECUTOR_CORE_POOL_SIZE,
                THREAD_POOL_EXECUTOR_MAX_POOL_SIZE,
                300, TimeUnit.SECONDS,
                LinkedBlockingQueue(10),
                SyncTaskThreadFactory(),
                SyncTaskRejectedExecutionHandler()
            )

    private inner class SyncTaskThreadFactory: ThreadFactory {
        private val prefix = "sync-task-pool-thread-"
        private val threadNumber = AtomicInteger(1)

        override fun newThread(r: Runnable?): Thread = Thread(r).apply {
            name = "$prefix${threadNumber.getAndIncrement()}"
        }
    }

    private inner class SyncTaskRejectedExecutionHandler: RejectedExecutionHandler {

        private val logger = LoggerFactory.getLogger(SyncTaskRejectedExecutionHandler::class.java)

        override fun rejectedExecution(r: Runnable?, executor: ThreadPoolExecutor?) {
            logger.error("sync task is started too rapidly! Your request has been aborted")
        }

    }
}