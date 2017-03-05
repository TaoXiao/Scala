package cn.gridx.springboot.zk.framework.registry;

import org.apache.zookeeper.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;

/**
 * Created by tao on 12/21/16.
 */

@Component
public class ServiceRegistryImpl implements ServiceRegistry , Watcher {
    private static Logger logger = LoggerFactory.getLogger(ServiceRegistryImpl.class);
    private static CountDownLatch latch = new CountDownLatch(1);
    private static final String REGISTRY_PATH = "/registry";
    private static final int    SESSION_TIMEOUT = 5*1000;
    private ZooKeeper zk;

    public ServiceRegistryImpl() {
        logger.info("空的构造函数");
    }

    public ServiceRegistryImpl(String zkServers) {
        logger.info("构造 ServiceRegistryImpl 实例, zkServers = {}", zkServers);
        try {
            logger.info("开始创建zk client: latch.getCount = " + latch.getCount());
            zk = new ZooKeeper(zkServers, SESSION_TIMEOUT, this);
            latch.await();
            logger.info("完成创建zk client: latch.getCount = " + latch.getCount());
        } catch (Exception ex) {
            logger.error("创建zookeeper client时发生异常: \n" + ex.getStackTrace());
        }
    }

    @Override
    public void register(String serviceName, String serviceAddress) {
        try {
            // 1) 创建root znode: "/registry" (永久节点)
            if (zk.exists(REGISTRY_PATH, false) == null) {
                zk.create(REGISTRY_PATH, "registry root znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("成功创建了znode: {} ", REGISTRY_PATH);
            } else
                logger.info("znode: {} 已存在", REGISTRY_PATH);

            // 2) 创建service znode: (永久节点)
            String servicePath = REGISTRY_PATH + "/" + serviceName;
            if (zk.exists(servicePath, false) == null) {
                zk.create(servicePath, "service znode".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
                logger.info("成功创建了znode: {} ", servicePath);
            } else
                logger.info("znode: {} 已存在", servicePath);

            // 3) 创建service address znode: (临时顺序节点,真实的地址放在data中)
            String addressPath = REGISTRY_PATH + "/address-" ;
            if (zk.exists(addressPath, false) == null) {
                zk.create(addressPath, serviceAddress.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
                logger.info("成功创建了znode: {} ", addressPath);
            } else
                logger.info("znode: {} 已存在", addressPath);
        } catch (Exception ex) {
            logger.error("发生异常: {}", ex.getStackTrace().toString());
        }
    }

    @Override
    public void process(WatchedEvent event) {
        logger.info("WatchedEvent = " + event.toString());
        if (event.getState() == Event.KeeperState.SyncConnected) {
            logger.info("will countdown latch");
            latch.countDown();
        }
    }
}
