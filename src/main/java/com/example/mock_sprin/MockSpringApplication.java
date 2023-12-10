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

    private final Map<String, String> endpoints;

    public MockController() throws IOException {
        String configPath = "src/main/resources/config.json";
        String configContent = new String(Files.readAllBytes(Paths.get(configPath)));
        endpoints = parseConfig(configContent);
    }

    private Map<String, String> parseConfig(String configContent) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        List<Config> configs = objectMapper.readValue(configContent, new TypeReference<List<Config>>() {});
        Map<String, String> endpoints = new HashMap<>();
        for (Config config : configs) {
            endpoints.put(config.getPath(), config.getResponseBody());
        }
        return endpoints;
    }

    @PostMapping(value = "/**", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> handleRequest(@RequestBody String requestBody) {
        String path = (String) RequestContextHolder.getRequestAttributes().getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
        String responseBody = endpoints.get(path);
        return ResponseEntity.ok(responseBody);
    }
}

class Config {
    private String name;
    private String path;
    private String responseBody;

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
}


