package com.sff.zk.client;

import org.apache.zookeeper.*;

import java.util.concurrent.CountDownLatch;

/**
 * 异步 api 创建持久化节点
 */
public class CreatedAsyncNode implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    public static void main(String[] args) throws Exception {

        ZooKeeper zooKeeper = new ZooKeeper(
                "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000,
                new CreatedAsyncNode());
        latch.await();

        //持久化节点：创建接口返回该节点路径，无返回值；异步创建的节点：/zk-test
        zooKeeper.create("/zk-test", "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT, new CallBack(), "PERSISTENT");

        //持久化顺序节点：会自动的在节点路径后加一个数字，该方法无返回值,创建后节点：/zk-test-seq0000000002
        zooKeeper.create("/zk-test-seq",
                "".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.PERSISTENT_SEQUENTIAL,
                new CallBack(),
                "PERSISTENT_SEQUENTIAL");

        Thread.sleep(1000);
    }

    public void process(WatchedEvent watchedEvent) {
        if (Watcher.Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            latch.countDown();
        }
    }

    static class CallBack implements AsyncCallback.StringCallback {
        /**
         * 服务端回调方法
         *
         * @param code 服务端响应码：0 接口调用成功；-4 客户端和服务端连接断开；-110 节点已存在 ；-112 会话过期
         * @param path 创建节点传入的路径参数
         * @param ctx  异步创建api传入的ctx参数
         * @param name 服务端真正创建节点的名称，业务逻辑应该以该值为准
         */
        public void processResult(int code, String path, Object ctx, String name) {
            System.out.println("created success : " + code + "," + path + "," + ctx + "," + name);
        }
    }
}

