package cn.gridx.springboot.zk.framework.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by tao on 12/21/16.
 */

@Configuration
@ConfigurationProperties(prefix = "registry")
public class RegistryConfig2 {
    private static Logger logger = LoggerFactory.getLogger(RegistryConfig2.class);
    private String zkServers;

    public RegistryConfig2() {
        logger.info("构造了 RegistryConfig2 实例");
    }

    @Bean
    public ServiceRegistry serviceRegistry() {
        logger.info("执行方法 serviceRegistry, 创建ServiceRegistryImpl实例");
        return new ServiceRegistryImpl(zkServers);
    }

    public void setZkServers(String zkServers) {
        logger.info("将 zkServers 设置为 " + zkServers);
        this.zkServers = zkServers;
    }
}
