package cn.gridx.springboot.zk2.sample;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Created by tao on 12/21/16.
 */
@SpringBootApplication(scanBasePackages = "cn.gridx.springboot.zk2")
public class SampleApplication {
    private static Logger logger = LoggerFactory.getLogger(SampleApplication.class);

    public SampleApplication() {
        logger.info("构造了 SampleApplication 实例");
    }

    /*
    public static void main(String[] args) {
        logger.info("进入 SampleApplication.main");
        ConfigurableApplicationContext appCtx = SpringApplication.run(SampleApplication.class, args);
        logger.info("完成调用 SpringApplication.run(SampleApplication.class, args)");
        regZk();
    }
    */

    public static void regZk() {
        logger.info("only me now ");
    }

}
