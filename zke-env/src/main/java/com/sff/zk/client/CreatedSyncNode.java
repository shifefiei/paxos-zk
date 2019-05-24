package com.sff.zk.client;

import org.apache.zookeeper.*;

import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 同步 api 创建节点,然后同步获取节点列表
 */
public class CreatedSyncNode implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);

    private static ZooKeeper zooKeeper = null;

    public static void main(String[] args) throws Exception {
        zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000, new CreatedSyncNode());
        latch.await();

        String nodePath = "/zk-data";
        zooKeeper.create(nodePath, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        List<String> list = zooKeeper.getChildren(nodePath, true);
        System.out.println("get children : " + list);

        //给节点 /zk-data 再添加子节点 /conf
        zooKeeper.create(nodePath + "/conf", "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

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
                } catch (KeeperException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
