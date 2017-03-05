package cn.gridx.zookeeper.app;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;

/**
 * Created by tao on 12/20/16.
 */
class Master implements Watcher {
    ZooKeeper zk;
    String connString;

    public Master(String connString) {
        this.connString = connString;
    }

    private void startZkClient() throws IOException {
        zk = new ZooKeeper(connString, 15*1000, this);
    }

    private boolean checkMaster(String path) throws InterruptedException {
        while (true) {
            try {
                Stat stat = new Stat();
                byte[] data = zk.getData(path, false, stat);
                System.out.println("znode \"" + path + "\" exists, data is :" + new String(data));
                return true;
            } catch (KeeperException.NoNodeException ex) {
                System.err.println("znode \"" +  path +  "\" does not exist yet:\n" + ex.getMessage());
                return false;
            } catch (KeeperException.ConnectionLossException ex) {
                System.err.println(ex.getMessage());
            } catch (KeeperException ex) {
                System.err.println(ex);
            }
        }
    }


    private void close() throws InterruptedException {
        zk.close();
    }

    @Override
    public void process(WatchedEvent e) {
        System.err.println("正在处理 .... : " + e);
    }

    private void createZNode(String path, String data) throws KeeperException, InterruptedException {
        String retPath = zk.create(path, data.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.err.println("创建znode成功: " + retPath);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        Master master = new Master("ecs1:2181");
        master.startZkClient();

        master.checkMaster("/master");

        try {
            master.createZNode("/master", "hello, i am master");
        } catch (KeeperException.NodeExistsException ex) {
            System.err.println("znode已经存在了:\n" + ex.getMessage());
        } catch (KeeperException ex) {
            System.err.println("异常发生了:\n" + ex.getMessage());
        }
        master.close();

        Thread.sleep(3*1000);

        System.out.println("结束");
    }
}
