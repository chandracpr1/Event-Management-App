package com.eventhub.auth.service;


import com.eventhub.auth.entity.User;
import com.eventhub.auth.repository.UserRepository;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;


@Service
public class JwtService {

    private final UserRepository userRepository;

    private RSAPrivateKey privateKey;
    private RSAPublicKey publicKey;

    public JwtService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void init() throws Exception  {
        privateKey = loadPrivateKey(System.getenv("JWT_PRIVATE_KEY"));
        publicKey = loadPublicKey(System.getenv("JWT_PUBLIC_KEY"));
    }

    private RSAPrivateKey loadPrivateKey(String key) throws Exception {

        if (key == null) {
            throw new RuntimeException("JWT_PRIVATE_KEY is not set");
        }

        String privateKeyPEM = key
                .replaceAll("-----[A-Z ]+-----", "")  // 1. Removes ANY header/footer automatically
                .replace("\"", "")                    // 2. Removes double quotes
                .replace("'", "")                     // 3. Removes single quotes
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) keyFactory.generatePrivate(new PKCS8EncodedKeySpec(encoded));
    }

    private RSAPublicKey loadPublicKey(String key) throws Exception {

        if (key == null) {
            throw new RuntimeException("JWT_PUBLIC_KEY is not set");
        }

        String publicKeyPEM = key
                .replaceAll("-----[A-Z ]+-----", "")
                .replace("\"", "")
                .replace("'", "")
                .replaceAll("\\s", "");
        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }

    public String generateToken(String userId, String email) throws Exception {

        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        JWSSigner signer = new RSASSASigner(privateKey);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("email", email)
                .claim("name",user.getName())
                .claim("roles",user.getRoles())
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + 3600000))
                .build();

        SignedJWT jwt = new SignedJWT(
                new JWSHeader(JWSAlgorithm.RS256),
                claims
        );

        jwt.sign(signer);
        return jwt.serialize();
    }

    public boolean validateToken(String token) throws Exception {
        SignedJWT jwt = SignedJWT.parse(token);

        JWSVerifier verifier = new RSASSAVerifier(publicKey);

        return jwt.verify(verifier) &&
                new Date().before(jwt.getJWTClaimsSet().getExpirationTime());
    }
}
