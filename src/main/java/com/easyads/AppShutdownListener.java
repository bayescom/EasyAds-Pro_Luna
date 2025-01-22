package com.easyads;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.InternalThreadLocalMap;
import org.apache.logging.log4j.LogManager;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class AppShutdownListener {
    @EventListener
    public void onApplicationShutdown(ContextClosedEvent event) {
        // 停止 MySQL 连接清理线程
        stopMySQLCleanupThread();

        // 停止 Log4j2 相关的线程
        stopLog4j2Threads();

        // 停止其他线程
        stopThreadLocal();
    }

    private void stopMySQLCleanupThread() {
        try {
            // 停止 MySQL 的 AbandonedConnectionCleanupThread
            System.out.println("Stopping MySQL AbandonedConnectionCleanupThread...");
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopLog4j2Threads() {
        try {
            // 停止 Log4j2 调度线程
            System.out.println("Stopping Log4j2 threads...");
            LogManager.shutdown();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void stopThreadLocal() {
        try {
            // 停止其他线程
            System.out.println("Stopping Internal Thread Local ...");
            InternalThreadLocalMap.remove();
            InternalThreadLocalMap.destroy();
            FastThreadLocal.removeAll();
            FastThreadLocal.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
