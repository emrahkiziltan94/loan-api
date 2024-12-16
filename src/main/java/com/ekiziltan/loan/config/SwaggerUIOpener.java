package com.ekiziltan.loan.config;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.awt.Desktop;
import java.net.URI;

@Component
@Profile("dev")
@Slf4j
public class SwaggerUIOpener implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${server.port}")
    private int serverPort;
    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        String url = "http://localhost:" + serverPort + contextPath + "/swagger-ui.html";
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                log.info("Swagger UI is opened in browser:  {}", url);
            } else {
                log.info("Swagger url: {} ", url);
            }
        } catch (Exception e) {
            log.error("Swagger UI could not be opened.");
            e.printStackTrace();
        }
    }
}
