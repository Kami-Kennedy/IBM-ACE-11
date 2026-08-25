package com.sochitel.security;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Formatter;
import java.util.logging.Logger;

public class SecurityUtils {

    private static final Logger log = Logger.getLogger(SecurityUtils.class.getName());

    private static final String SECRET_KEY =
        "6VxOu1a4lgJkyPMi4cO1BftyDaVrK2w9oO_r_in9vGI=";

    // -------------------------------------------------------------------------
    // Main — smoke test
    // -------------------------------------------------------------------------
    public static void main(String[] args) {
        System.out.println("===== SecurityUtils Test =====\n");

        String nonce = getNonceAsString("");
        System.out.println("Nonce (13-digit ms): " + nonce);

        String payload = "{\"Channel\":\"LEO\",\"SourceAccountNumber\":\"990530000019\","
                       + "\"DestinationCardPan\":\"469617006507829\",\"Amount\":\"105\"}";
        System.out.println("Payload:             " + payload);

        String signature = sign(payload, nonce, SECRET_KEY);
        System.out.println("HMAC-SHA256 Sig:     " + signature);

        boolean isValid    = validate(payload, nonce, signature, SECRET_KEY).equals("true");
        System.out.println("Signature Valid:     " + isValid);

        boolean isTampered = validate(payload + "tampered", nonce, signature, SECRET_KEY).equals("true");
        System.out.println("Tampered Valid:      " + isTampered);

        System.out.println("\n===== Done =====");
    }

    // -------------------------------------------------------------------------
    // Nonce
    // -------------------------------------------------------------------------
    public static String getNonceAsString(String dummy) {
        return String.valueOf(System.currentTimeMillis());
    }

    // -------------------------------------------------------------------------
    // sign()  —  formerly computeSignature() in the new code
    //
    //  Computes HMAC-SHA256 over (payload + nonce), returns lowercase hex.
    //  Logs all three inputs so you can reproduce the signature in debug.
    // -------------------------------------------------------------------------
    public static String sign(String payload, String nonce, String secretKey) {
        log.info("sign() → payload: " + payload);
        log.info("sign() → nonce:   " + nonce);
        log.info("sign() → key:     " + secretKey);

        try {
            String key           = (secretKey == null || secretKey.isEmpty()) ? SECRET_KEY : secretKey;
            String payloadString = payload + nonce;          // matches new SignatureService logic
            log.info("sign() → payloadString: " + payloadString);

            return hmacSha256(payloadString, key);
        } catch (Exception e) {
            log.severe("sign() error: " + e.getMessage());
            return "SIGN_ERROR:" + e.getMessage();           // safe string — ACE can inspect it
        }
    }

    // -------------------------------------------------------------------------
    // validate()  —  formerly verifySignature() in the new code
    //
    //  Uses MessageDigest.isEqual() for constant-time comparison (timing-safe).
    //  Returns "true" / "false" strings so ACE ESQL callers are unaffected.
    // -------------------------------------------------------------------------
    public static String validate(
            String payload,
            String nonce,
            String incomingSignature,
            String secretKey) {

        // Guard: null inputs → fast-fail (mirrors new code's null check)
        if (nonce == null || incomingSignature == null || secretKey == null) {
            log.info("validate() → null guard hit (nonce / signature / key)");
            return "false";
        }

        try {
            log.info("validate() → receivedSignature: " + incomingSignature);

            String key      = (secretKey.isEmpty()) ? SECRET_KEY : secretKey;
            String expected = sign(payload, nonce, key);

            log.info("validate() → recomputed: " + expected);

            // Bubble up sign errors so the caller can see them
            if (expected != null && expected.startsWith("SIGN_ERROR:")) {
                return expected;
            }
            if (expected == null || expected.isEmpty()) {
                return "NULL_EXPECTED";
            }

            // ✅ Constant-time byte comparison — prevents timing attacks
            boolean match = MessageDigest.isEqual(
                expected.getBytes(StandardCharsets.UTF_8),
                incomingSignature.getBytes(StandardCharsets.UTF_8)
            );
            return String.valueOf(match);

        } catch (Exception e) {
            log.severe("validate() error — message:  " + e.getMessage());
            log.severe("validate() error — cause:    " + e.getCause());
            log.severe("validate() error — toString: " + e);
            e.printStackTrace();
            return "VALIDATE_ERROR:" + e.getMessage();
        }
    }

    // -------------------------------------------------------------------------
    // Internal HMAC-SHA256 helper
    // -------------------------------------------------------------------------
    private static String hmacSha256(String data, String secretKey) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec keySpec = new SecretKeySpec(
            secretKey.getBytes(StandardCharsets.UTF_8),
            "HmacSHA256"
        );
        mac.init(keySpec);
        byte[] rawHmac = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHex(rawHmac);
    }

    private static String toHex(byte[] bytes) {
        Formatter formatter = new Formatter();
        for (byte b : bytes) {
            formatter.format("%02x", b);
        }
        String hex = formatter.toString();
        formatter.close();
        return hex;
    }
}