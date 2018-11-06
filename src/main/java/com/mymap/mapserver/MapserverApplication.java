package com.mymap.mapserver;

import com.mymap.coremap.ServerBackend.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MapserverApplication {

    private static final Logger logger = LoggerFactory.getLogger(MapserverApplication.class);
    private static CoreMap map = null;
    private static String API_KEY;
    @Value("${file}")
    private String file;

    public static String getApiKey() {
        return API_KEY;
    }

    public static CoreMap getMap() {
        return map;
    }

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(MapserverApplication.class);
        app.run(args);
    }

    public static Logger getLogger() {
        return logger;
    }

    @Value("${API_KEY}")
    public void setAPI_KEY(String key) {
        API_KEY = key;
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    //    @EventListener(ApplicationReadyEvent.class)
//    public void doSomethingAfterStartup() {
//        logger.info(String.format("Start loading map: %s", file));
//        map = new CoreMap(file);
//        logger.info(String.format("Finish loading map: %s", file));
//    }
    @Component
    public class StartUpInit {
        @PostConstruct
        public void init() {
            logger.info(String.format("Start loading map: %s", file));
            map = new CoreMap(file);
            logger.info(String.format("Finish loading map: %s", file));
        }
    }
}
