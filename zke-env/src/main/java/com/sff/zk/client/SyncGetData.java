package com.sff.zk.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;

/**
 * 同步获取节点数据
 */
public class SyncGetData implements Watcher {

    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper = null;
    private static Stat stat = new Stat();

    public static void main(String[] args) throws Exception {
        String path = "/zk-demo";
        zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000,
                new SyncGetData());
        latch.await();

        zooKeeper.create(path, "123".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        //直接获取节点数据
        byte[] datas = zooKeeper.getData(path, true, stat);
        System.out.println("结果：" + new String(datas));
        System.out.println(stat);

        //修改节点数据
        zooKeeper.setData(path, "123".getBytes(), -1);
        Thread.sleep(1000);
    }

    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                latch.countDown();
                //节点变化时进行通知，EventType.NodeDataChanged 事件
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    byte[] datas = zooKeeper.getData(watchedEvent.getPath(), true, stat);
                    System.out.println("回调通知：" + new String(datas));
                    System.out.println(stat);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
