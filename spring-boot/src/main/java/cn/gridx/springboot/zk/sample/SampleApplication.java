package cn.gridx.springboot.zk.sample;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * Created by tao on 12/21/16.
 */
@RestController
@SpringBootApplication(scanBasePackages = "cn.gridx.springboot.zk")
public class SampleApplication {
    /*
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
    */


    @RequestMapping(name = "HelloService", method = RequestMethod.GET, path = "hello")
    public String hello() {
        return "hello - " + new Date().toString();
    }

}
