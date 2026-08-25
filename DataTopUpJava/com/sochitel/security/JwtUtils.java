package com.sochitel.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

import java.util.Date;

public class JwtUtils {

    // 🔑 Credentials
    private static final String VALID_USERNAME = "EsbVasUsr";
    private static final String VALID_PASSWORD = "tsunami";

    // 🔐 32-byte HEX secret (64 hex chars)
    private static final String SECRET_KEY_HEX =
        "3953432ebd391c3cabcfd9e397c5cdcc34fac494e8fd876a11c70261652dea43";

    // ⏱ 60 minutes
    private static final long EXPIRATION_TIME = 60 * 60 * 1000;

    /**
     * ✅ Generate JWT Token (Only for valid credentials)
     */
    public static String generateToken(String username, String password) {
        try {
            if (!VALID_USERNAME.equals(username) || !VALID_PASSWORD.equals(password)) {
                return "ERROR:Invalid credentials";
            }

            Algorithm algorithm = Algorithm.HMAC256(hexToBytes(SECRET_KEY_HEX));

            return JWT.create()
                    .withSubject(username)
                    .withIssuer("ESB-Gateway")
                    .withIssuedAt(new Date())
                    .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                    .sign(algorithm);

        } catch (Exception e) {
            return "ERROR:" + e.getMessage();
        }
    }

    /**
     * ✅ Validate JWT Token
     */
    public static String validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(hexToBytes(SECRET_KEY_HEX));

            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("ESB-Gateway")
                    .build();

            DecodedJWT decodedJWT = verifier.verify(token);

            if (decodedJWT.getExpiresAt().before(new Date())) {
                return "false";
            }

            return "true";

        } catch (Exception e) {
            return "false";
        }
    }

    /**
     * 🔁 HEX → byte[]
     */
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) (
                (Character.digit(hex.charAt(i), 16) << 4)
              + Character.digit(hex.charAt(i+1), 16)
            );
        }
        return data;
    }
}