package com.example.mock_sprin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.servlet.HandlerMapping;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class MockSpringApplication {

    public static void main(String[] args) {
        SpringApplication.run(MockSpringApplication.class, args);
    }
}

@RestController
@RequestMapping("/")
class MockController {

    private final Map<String, Config> endpoints;

    public MockController() throws IOException {
        String configPath = "src/main/resources/config.json";
        String configContent = new String(Files.readAllBytes(Paths.get(configPath)));
        endpoints = parseConfig(configContent);
    }

    private Map<String, Config> parseConfig(String configContent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Config> configs = objectMapper.readValue(configContent, new TypeReference<List<Config>>() {});
        Map<String, Config> endpoints = new HashMap<>();
        for (Config config : configs) {
            endpoints.put(config.getPath(), config);
        }
        return endpoints;
    }

    @PostMapping(value = "/**", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleRequest(@RequestBody String requestBody) {
        String path = (String) RequestContextHolder.getRequestAttributes().getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        Config config = endpoints.get(path);
        String responseBody = config.getResponseBody();
        long delay = config.getDelay();

        // Обработка самой задержки из конф
        try {
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return ResponseEntity.ok(responseBody);
    }
}


class Config {
    private String name;
    private String path;
    private String responseBody;
    private  long delay;

    // Геттеры
    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getResponseBody() {
        return responseBody;
    }
    public long getDelay() {return delay;}

    // Сеттеры
    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
    public void setDelay(long delay) {this.delay = delay;}
}


