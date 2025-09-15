package com.example.bank_rest_test_task.security.jwt;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Configuration
public class JwtKeyConfiguration {

    @Getter
    private final Long accessExpiration;
    @Getter
    private final Long refreshExpiration;

    private final String keyPath;

    public JwtKeyConfiguration(@Value("${jwt.access.expiration}") Long accessExpiration,
                               @Value("${jwt.refresh.expiration}") Long refreshExpiration,
                               @Value("${jwt.keys.path}") String keyPath) {
        this.accessExpiration = accessExpiration;
        this.refreshExpiration = refreshExpiration;
        this.keyPath = keyPath;
    }
    
    @PostConstruct
    public void checkKeys() {
        Path privateKeyPath = Paths.get(keyPath, "private-key.pem");
        Path publicKeyPath = Paths.get(keyPath, "public-key.pem");

        if (!Files.exists(privateKeyPath) || !Files.isReadable(privateKeyPath) ||
                !Files.exists(publicKeyPath) || !Files.isReadable(publicKeyPath)) {
            throw new IllegalStateException(
                    "CRITICAL ERROR: JWT keys are missing or not readable.\n" +
                            "Private key path: " + privateKeyPath.toAbsolutePath() + "\n" +
                            "Public key path: " + publicKeyPath.toAbsolutePath()
            );
        }
    }

    @Bean
    public KeyPair keyPair() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        String privateKeyPEM = new String(Files.readAllBytes(Paths.get(keyPath, "private-key.pem")))
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        String publicKeyPEM = new String(Files.readAllBytes(Paths.get(keyPath, "public-key.pem")))
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyPEM);
        byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory kf = KeyFactory.getInstance("RSA");
        return new KeyPair(
                kf.generatePublic(new X509EncodedKeySpec(publicKeyBytes)),
                kf.generatePrivate(new PKCS8EncodedKeySpec(privateKeyBytes))
        );
    }
}









