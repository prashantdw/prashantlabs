package com.prashantlabs.integrations.webhook;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/webhooks")
public class WebhookController {
    private final WebhookSignatureService signer;
    private final Set<String> seen = ConcurrentHashMap.newKeySet();

    public WebhookController(@Value("${app.webhook.secret}") String secret) {
        this.signer = new WebhookSignatureService(secret);
    }

    @PostMapping("/payment")
    public ResponseEntity<String> payment(@RequestBody String body,
                                          @RequestHeader(name = "X-Signature", required = false) String sig,
                                          @RequestHeader(name = "Idempotency-Key", required = false) String id) {

        if (sig == null || !signer.verify(body, sig))
            return ResponseEntity.status(401).body("invalid signature");
        if (id != null && !seen.add(id)) return ResponseEntity.ok("duplicate ignored");
        return ResponseEntity.ok("ok");
    }
}
