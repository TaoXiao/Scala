package cn.gridx.springboot.zk.framework.registry;

/**
 * Created by tao on 12/21/16.
 */
public interface ServiceRegistry {
    void register(String serviceName, String serviceAddress);
}
