package cn.gridx.springboot.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Created by tao on 12/21/16.
 */

@Component
public class Car {
    private static Logger logger = LoggerFactory.getLogger(Car.class);

    public Car() {
        logger.info("创建了一个Car instance");
    }

    public String toString() {
        return "I'am a car";
    }
}
