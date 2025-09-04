package com.prashantlabs.integrations.web;

import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequestMapping("/proxy/docs")
public class DocsProxyController {
    private final RestTemplate http;
    public DocsProxyController(RestTemplate http) { this.http = http; }

    private static final Map<String, String> TARGETS = Map.of(
            "documents",   "http://localhost:8081/v3/api-docs",
            "invoicing",   "http://localhost:8082/v3/api-docs",
            "integrations","http://localhost:8083/v3/api-docs"
    );

    @GetMapping("/{service}")
    public ResponseEntity<String> proxy(@PathVariable String service) {
        String target = TARGETS.get(service);
        if (target == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"error\":\"unknown service\"}");
        }
        ResponseEntity<String> resp = http.exchange(target, HttpMethod.GET, null, String.class);
        return ResponseEntity.status(resp.getStatusCode())
                .contentType(MediaType.APPLICATION_JSON)
                .body(resp.getBody());
    }
}
