package top.snowphoenix.toolsetencodetransformer.utils;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.MalformedClaimException;
import org.jose4j.jwt.NumericDate;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import top.snowphoenix.toolsetencodetransformer.config.JwtConfig;
import top.snowphoenix.toolsetencodetransformer.model.AuthLevel;
import top.snowphoenix.toolsetencodetransformer.model.CurrentUserInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.List;

@Component
public class JwtUtil {
    private final PublicKey publicKey;
    private final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    public JwtUtil(JwtConfig jwtConfig) throws NoSuchAlgorithmException, IOException, InvalidKeySpecException {
        byte[] publicFileBytes = readPemFile(jwtConfig.getPublicKeyPath());
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicFileBytes);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        this.publicKey = keyFactory.generatePublic(publicSpec);
    }

    private byte[] readPemFile(String path) throws IOException {
        List<String> lines = Files.readAllLines(Paths.get(path));
        StringBuilder sb = new StringBuilder();
        lines.stream().filter(s -> s.charAt(0) != '-').forEach(sb::append);
        return Base64.getDecoder().decode(sb.toString());
    }

    /***
     * 验证JWT，如果验证失败，返回null，否则返回其Payload中的信息。
     * 返回的Payload信息中，notBefore和auth可能会null，其余都不为null。
     *
     * @param jwt JWT字符串
     * @return 失败返回null，成功返回其中Payload中的信息
     */
    public CurrentUserInfo validateToken(String jwt) {

        // Use JwtConsumerBuilder to construct an appropriate JwtConsumer, which will
        // be used to validate and process the JWT.
        // The specific validation requirements for a JWT are context dependent, however,
        // it typically advisable to require a (reasonable) expiration time, a trusted issuer, and
        // and audience that identifies your system as the intended recipient.
        // If the JWT is encrypted too, you need only provide a decryption key or
        // decryption key resolver to the builder.
        JwtConsumer jwtConsumer = new JwtConsumerBuilder()
                .setEvaluationTime(
                        NumericDate.fromMilliseconds(
                                LocalDateTime.now().toInstant(ZoneOffset.ofHours(8)).toEpochMilli()))
                .setRequireExpirationTime() // the JWT must have an expiration time
                .setRequireIssuedAt() // the JWT must have an issued time
                .setVerificationKey(this.publicKey) // verify the signature with the public key
                .setJwsAlgorithmConstraints( // only allow the expected signature algorithm(s) in the given context
                        AlgorithmConstraints.ConstraintType.PERMIT,
                        AlgorithmIdentifiers.RSA_USING_SHA256) // which is only RS256 here
                .build(); // create the JwtConsumer instance

        try
        {
            //  Validate the JWT and process it to the Claims
            JwtClaims jwtClaims = jwtConsumer.processToClaims(jwt);
            Long uid = jwtClaims.getClaimValue("uid", Long.class);
            if (uid == null) {
                logger.error("Valid JWT without uid payload");
                return null;
            }
            String auth = jwtClaims.getClaimValueAsString("auth");
            if (auth == null) {
                logger.error("Valid JWT without auth payload");
                return null;
            }
            AuthLevel authLevel = AuthLevel.ofName(auth);
            if (authLevel == null) {
                logger.error("Valid JWT with wrong auth");
                return null;
            }
            CurrentUserInfo currentUserInfo = CurrentUserInfo.builder()
                    .uid(Math.toIntExact(uid))
                    .auth(authLevel)
                    .build();
            logger.debug("JWT validation succeeded! " + jwtClaims);
            return currentUserInfo;
        }
        catch (MalformedClaimException e) {
            logger.error("Valid JWT with wrong type", e);
            return null;
        }
        catch (InvalidJwtException e) {
            // InvalidJwtException will be thrown, if the JWT failed processing or validation in anyway.
            // Hopefully with meaningful explanations(s) about what went wrong.
            logger.info("Invalid JWT! ", e);

            // Programmatic access to (some) specific reasons for JWT invalidity is also possible
            // should you want different error handling behavior for certain conditions.

            // Whether or not the JWT has expired being one common reason for invalidity
            if (e.hasExpired())
            {
                try {
                    NumericDate exp = e.getJwtContext().getJwtClaims().getExpirationTime();
                    logger.debug("JWT expired at " + exp);
//                    throw new TokenExpiredException(TimeUtil.numericDateToLocalDateTime(exp));
                }
                catch (MalformedClaimException me) {
                    logger.debug("JWT expired and has wrong expire type");
                }
            }
            return null;
        }
    }
}
