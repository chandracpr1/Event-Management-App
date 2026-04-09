

package com.eventhub.gateway.util;

import com.eventhub.gateway.dto.AuthenticationDetails;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.annotation.PostConstruct;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import org.slf4j.Logger;

@Component
public class JwtUtils {

    private RSAPublicKey publicKey;

    private static final Logger logger= LoggerFactory.getLogger(JwtUtils.class);

    @PostConstruct
    public void init() throws Exception {

        String key = System.getenv("JWT_PUBLIC_KEY");
        if (key == null) throw new RuntimeException("JWT_PUBLIC_KEY not set");

        String publicKeyPEM = key
                .replaceAll("-----[A-Z ]+-----", "")
                .replace("\"", "")
                .replace("'", "")
                .replaceAll("\\s", "");

        byte[] encoded = Base64.getDecoder().decode(publicKeyPEM);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(encoded));
    }

    public boolean isTokenInvalid(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWSVerifier verifier = new RSASSAVerifier(publicKey);

            // Verify signature and expiration
            return !signedJWT.verify(verifier) ||
                    new Date().after(signedJWT.getJWTClaimsSet().getExpirationTime());
        } catch (Exception e) {
            return true;
        }
    }

    public String getUserId(String token) throws Exception {
        return SignedJWT.parse(token).getJWTClaimsSet().getSubject();
    }

    public AuthenticationDetails getAuthenticationDetails(String token) throws Exception {

        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claims = signedJWT.getJWTClaimsSet();


        String userId = claims.getSubject();
        String name = claims.getStringClaim("name");
        String email = claims.getStringClaim("email");


        List<String> rolesList = claims.getStringListClaim("roles");
        String roles = null;

        if (rolesList != null && !rolesList.isEmpty()) {
            roles = String.join(",", rolesList);
        } else {

            roles = claims.getStringClaim("roles");
        }

        AuthenticationDetails authDetails = AuthenticationDetails.builder()
                .userId(userId)
                .name(name)
                .roles(roles)
                .email(email)
                .build();
        logger.info("Extracted Details from JWT {}",authDetails);
        return authDetails;
    }
}
