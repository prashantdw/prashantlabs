package com.prashantlabs.integrations.webhook;

import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class WebhookSignatureService {
    private final String secret;

    public WebhookSignatureService(String s) {
        this.secret = s;
    }

    public String hmac(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Hex.encodeHexString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean verify(String payload, String sig) {
        return hmac(payload).equalsIgnoreCase(sig);
    }
}
