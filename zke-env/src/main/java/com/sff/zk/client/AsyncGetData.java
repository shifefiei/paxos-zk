package com.sff.zk.client;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.concurrent.CountDownLatch;


/**
 * 异步获取节点数据
 */
public class AsyncGetData implements Watcher {
    private static CountDownLatch latch = new CountDownLatch(1);
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws Exception {
        String path = "/zk-async";
        zooKeeper = new ZooKeeper("127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183",
                5000,
                new AsyncGetData());
        latch.await();
        zooKeeper.create(path, "456".getBytes(),
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

        //设置异步获取节点数据
        zooKeeper.getData(path, true, new DataCallback(), null);
        //再次更新节点数据
        zooKeeper.setData(path, "789".getBytes(), -1);

        Thread.sleep(1000);
    }

    public void process(WatchedEvent watchedEvent) {
        if (Event.KeeperState.SyncConnected == watchedEvent.getState()) {
            if (Event.EventType.None == watchedEvent.getType() && null == watchedEvent.getPath()) {
                latch.countDown();
                //节点变化时进行通知，EventType.NodeDataChanged 事件
            } else if (watchedEvent.getType() == Event.EventType.NodeDataChanged) {
                try {
                    //再次异步获取变化后的节点数据
                    zooKeeper.getData(watchedEvent.getPath(), true, new DataCallback(), null);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //接收服务端回调
    static class DataCallback implements AsyncCallback.DataCallback {
        public void processResult(int code, String path, Object ctx, byte[] data, Stat stat) {

            System.out.println("回调通知的节点数据：" + new String(data));
            System.out.println("code : " + code + ";path : " + path + ";stat : " + stat);
        }
    }
}
