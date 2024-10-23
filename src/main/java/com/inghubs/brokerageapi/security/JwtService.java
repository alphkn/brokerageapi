package com.inghubs.brokerageapi.security;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;


/**
 * Service for handling JWT operations, including token generation and validation.
 */
@Service
public class JwtService {
    @Value("${application.security.jwt.secret-key}")
    private String secretKey; // Secret key for signing JWT tokens

    @Value("${application.security.jwt.expiration}")
    private long jwtExpiration; // Expiration time for access tokens

    @Value("${application.security.jwt.refresh-token.expiration}")
    private long refreshExpiration; // Expiration time for refresh tokens

    /**
     * Extracts the username from the JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Generates a JWT token for a given user.
     *
     * @param userDetails the user details for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails); // Generate token without extra claims
    }

    /**
     * Generates a JWT token with additional claims.
     *
     * @param extraClaims  additional claims to include in the token
     * @param userDetails  the user details for which the token is generated
     * @return the generated JWT token
     */
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration); // Build token with expiration
    }

    /**
     * Constructs the JWT token using claims, subject, issued date, expiration date, and signature.
     *
     * @param extraClaims additional claims to include in the token
     * @param userDetails the user details for which the token is generated
     * @param expiration  the expiration time of the token
     * @return the constructed JWT token
     */
    private String buildToken(Map<String, Object> extraClaims, UserDetails userDetails, long expiration) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Validates the JWT token against the provided user details.
     *
     * @param token       the JWT token to validate
     * @param userDetails the user details to validate against
     * @return true if the token is valid; false otherwise
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Checks if the JWT token has expired.
     *
     * @param token the JWT token to check
     * @return true if the token is expired; false otherwise
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extracts the expiration date from the JWT token.
     *
     * @param token the JWT token
     * @return the expiration date
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extracts a specific claim from the JWT token using a provided resolver function.
     *
     * @param token          the JWT token
     * @param claimsResolver the function to extract the claim
     * @param <T>           the type of the claim
     * @return the extracted claim
     */
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extracts all claims from the JWT token.
     *
     * @param token the JWT token
     * @return the claims contained in the token
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Retrieves the signing key used for signing the JWT tokens.
     *
     * @return the signing key
     */
    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}