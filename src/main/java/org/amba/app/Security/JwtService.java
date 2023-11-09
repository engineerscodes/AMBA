package org.amba.app.Security;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {


    @Value("${jwt.key.private}")
    private String PRIVATE_KEY ;

    private static final Long ONE_YEAR =  (1000L * 60 * 60 * 24 * 30) ; // if 30 days


    public SecretKey getPrivateKey(){
        byte[] privateKeyBytes = Decoders.BASE64.decode(PRIVATE_KEY);
        SecretKey key = Keys.hmacShaKeyFor(privateKeyBytes);
        return Keys.hmacShaKeyFor(privateKeyBytes);
    }


    public <T> T getClaims(String token, Function <Claims , T> claimResolver ){
        final  Claims claims = getAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String getUsername(String jwtToken){
        return getClaims(jwtToken,Claims::getSubject);
    }

    public String generateToken(UserDetails user){
        HashMap <String,Object> newClaims = new HashMap<>();
        newClaims.put("roles",user.getAuthorities());
        newClaims.put("name",user.getUsername());
        return generateToken(newClaims,user);
    }


    public boolean isValidToken(String token,UserDetails user){
       final String UserName = getUsername(token); // email is the userName
       return (UserName.equals(user.getUsername()) && !isExpiredToken(token));
    }

    public boolean isExpiredToken(String token ){
        return getExpirationDate(token).before(new Date());
    }

    public Date getExpirationDate(String token){
        return getClaims(token,Claims::getExpiration);
    }

    public String generateToken(Map<String , Object> newClaims, UserDetails user){
         return Jwts.builder().claims(newClaims).subject(user.getUsername())
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis() + ONE_YEAR))
                 .signWith(getPrivateKey(), Jwts.SIG.HS256)  // https://github.com/jwtk/jjwt#signaturealgorithm-override
                 .compact();
    }

    private Claims getAllClaims(String token){
        // throw JwtException - runtime exception unchecked
        // https://github.com/jwtk/jjwt#constant-parsing-key
        return Jwts.parser().verifyWith(getPrivateKey()).build().parseSignedClaims(token).getPayload();
    }

}
