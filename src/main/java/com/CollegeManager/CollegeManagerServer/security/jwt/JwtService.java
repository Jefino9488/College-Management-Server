package com.CollegeManager.CollegeManagerServer.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${application.security.jwt.secretKey}")
    private String secretKey;

    @Value("${application.security.jwt.jwtExpiration}")
    private Long jwtExpiration;

    public String generateToken(HashMap<String,Object> claims, UserDetails userDetails) {
        return buildToken(claims,userDetails,jwtExpiration);
    }

    private String buildToken(HashMap<String,Object> extraClaims,
                              UserDetails userDetails,
                              Long jwtExpiration){

        var authorities =userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts
                .builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .claim("authorities",authorities)
                .signWith(getSignInKey())
                .compact();

    }

    protected Key getSignInKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64URL.decode(secretKey));
    }

    public boolean validateToken(String token,UserDetails userDetails){
        final String inputUserEmail = extractUserEmailFromToken(token);
        if(inputUserEmail==null){
            return false;
        }
        return Objects.equals(inputUserEmail,userDetails.getUsername()) &&
                !extractExpirationFromToken(token).before(new Date());
    }

    public String extractUserEmailFromToken(String token) {
        return extractClaims(token,Claims::getSubject);
    }

    private Date extractExpirationFromToken(String token){
        return extractClaims(token,Claims::getExpiration);
    }
    private <T> T extractClaims(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = Jwts
                .parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claimsTFunction.apply(claims);
    }

}