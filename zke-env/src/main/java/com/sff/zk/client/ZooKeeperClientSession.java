package com.sff.zk.client;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.util.concurrent.CountDownLatch;

/**
 * 创建 zookeeper 会话
 * <p>
 * <p>
 * zookeeper 客户端 和 服务端创建会话的过程是异步的。也就是客户度通过构造方法创建会话后立即返回，此时的连接并没有完全建立。
 * 当真正的会话建立完成后，zk服务端会给客户端通知一个事件，客户端获取通知之后在表明连接正在建立。
 */
public class ZooKeeperClientSession implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {
        ZooKeeper zooKeeper = new ZooKeeper("127.0.0.1:2183", 5000, new ZooKeeperClientSession());
        System.out.println(zooKeeper.getState());
        latch.await();

        long sessionId = zooKeeper.getSessionId();
        byte[] sessionPasswd = zooKeeper.getSessionPasswd();
        System.out.println(zooKeeper.getSessionId());

        /**
         *  利用 sessionId 和 sessionPasswd 复用会话连接
         */
        ZooKeeper zooKeeper1 = new ZooKeeper("127.0.0.1:2183",
                5000,
                new ZooKeeperClientSession(),
                sessionId,
                sessionPasswd);
        System.out.println(zooKeeper1.getSessionId());
    }

    /**
     * 处理 zookeeper 服务端的 Watcher 通知
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        System.out.println("receive watch event : " + watchedEvent);
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }
}
