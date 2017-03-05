package cn.gridx.springboot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.atomic.AtomicLong;


@SpringBootApplication
@RestController
public class SampleController {
    private static Logger logger = LoggerFactory.getLogger(SampleController.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();


    @RequestMapping(method = RequestMethod.GET, path = "/home")
    public String home() throws InterruptedException {
        logger.info("[home] 收到请求");
        Thread.sleep(10*1000);
        logger.info("[home] 返回响应");
        return "Hello World!";
    }

    @RequestMapping(method = RequestMethod.GET, path = "/greeting")
    public Greeting greeting(@RequestParam(value="name", defaultValue="World") String name) throws InterruptedException {
        logger.info("[greeting] 收到请求");
        Thread.sleep(10*1000);
        logger.info("[greeting] 返回响应");
        return new Greeting(counter.incrementAndGet(),
                String.format(template, name));
    }

    public static void main(String[] args) throws Exception {
        SpringApplication.run(SampleController.class, args);
    }

}