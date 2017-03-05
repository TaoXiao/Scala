package cn.gridx.springboot.zk.framework.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Map;

/**
 * Created by tao on 12/21/16.
 */
@Component
public class WebListener implements ServletContextListener {
    private static Logger logger = LoggerFactory.getLogger(WebListener.class);

    public WebListener() {
        super();
        logger.info("构造了 WebListener 实例");
    }

    @Value("${server.address}")
    private String serverAddress;

    @Value("${server.port}")
    private int serverPort;

    @Autowired
    private ServiceRegistry serviceRegistry ;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        logger.info("调用了 contextInitialized");
        ServletContext servletCtx = event.getServletContext();
        ApplicationContext appCtx = WebApplicationContextUtils.getRequiredWebApplicationContext(servletCtx);
        RequestMappingHandlerMapping mapping = appCtx.getBean(RequestMappingHandlerMapping.class);
        Map<RequestMappingInfo, HandlerMethod> infoMap = mapping.getHandlerMethods();
        for (RequestMappingInfo info : infoMap.keySet()) {
            logger.info("info.getName = " + info.getName());
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("调用了 contextDestroyed");
    }

}
