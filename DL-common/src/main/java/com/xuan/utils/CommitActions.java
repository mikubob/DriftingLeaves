package com.xuan.utils;

import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 事务提交回调工具，回滚时不执行副作用，无事务同步时立即执行。
 */
public final class CommitActions {
    private CommitActions() {}
    /**
     * 在当前事务成功提交后执行副作用；无事务同步时立即执行。
     * @param action 提交后动作，异常继续向调用方传播
     */
    public static void afterCommit(Runnable action) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            action.run();
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCommit() { action.run(); }
        });
    }
}
