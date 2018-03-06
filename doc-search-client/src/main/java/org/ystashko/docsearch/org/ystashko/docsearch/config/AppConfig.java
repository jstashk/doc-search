package org.ystashko.docsearch.org.ystashko.docsearch.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.ystashko.docsearch.service.DocumentService;
import org.ystashko.docsearch.service.RestDocumentService;

@Configuration
public class AppConfig {

    @Value("${docsearch.server.location}")
    private String serverLocation;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public DocumentService assetService(RestTemplate restTemplate) {
        return new RestDocumentService(serverLocation, restTemplate);
    }

}
