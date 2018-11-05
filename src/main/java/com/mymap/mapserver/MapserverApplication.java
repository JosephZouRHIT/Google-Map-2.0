package com.mymap.mapserver;

import com.mymap.coremap.ServerBackend.CoreMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class MapserverApplication {

    private static CoreMap map = null;
    private static final Logger logger = LoggerFactory.getLogger(MapserverApplication.class);

    @Value("${file}")
    private String file;

    private static String API_KEY;

    public static String getApiKey() {
        return API_KEY;
    }

    @Value("${API_KEY}")
    public void  setAPI_KEY(String key) {
        API_KEY = key;
    }

    public static CoreMap getMap() {
        return map;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void doSomethingAfterStartup() {
        logger.info(String.format("Start loading map: %s", file));
        map = new CoreMap(file);
        logger.info(String.format("Finish loading map: %s", file));
    }

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(MapserverApplication.class, args);
    }

    public static Logger getLogger() {
        return logger;
    }
}