package com.sff.zk.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 异步查询节点列表
 */
public class AsyncQueryNode implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {
        //创建异步会话
        zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000, new AsyncQueryNode());
        latch.await();

        String nodePath = "/zk-book";
        zooKeeper.create(nodePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //表示异步获取子节点列表，在 ChildrenCallback 中对节点列表进行处理
        zooKeeper.getChildren(nodePath, true, new ChildrenCallback(), "query children");

        //给节点 /zk-data 再添加子节点 /conf
        zooKeeper.create(nodePath + "/src", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        Thread.sleep(1000);
    }

    /**
     * 监听子节点的变化
     *
     * @param watchedEvent
     */
    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                latch.countDown();
            } else if (watchedEvent.getType() == Event.EventType.NodeChildrenChanged) {
                //监听到子节点变化后，主动的去获取节点列表
                try {
                    List<String> list = zooKeeper.getChildren(watchedEvent.getPath(), true);
                    System.out.println("get children node changed : " + list);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    static class ChildrenCallback implements AsyncCallback.Children2Callback {
        /**
         * @param code 服务端响应码
         * @param path 创建节点传入的路径参数
         * @param ctx  上下文信息
         * @param list 子节点列表
         * @param stat 节点的状态信息
         */
        public void processResult(int code, String path, Object ctx, List<String> list, Stat stat) {
            System.out.println("code : " + code
                    + ";path : " + path
                    + ";ctx : " + ctx
                    + ";children list : " + list
                    + ";stat : " + stat);
        }
    }
}
