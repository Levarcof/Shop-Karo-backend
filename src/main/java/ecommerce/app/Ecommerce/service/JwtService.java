package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.entity.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class JwtService {
    private JwtEncoder jwtEncoder;

    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.expiry}")
    private Long expiry;

    public  JwtService(JwtEncoder jwtEncoder){
        this.jwtEncoder = jwtEncoder;
    }


 public String createToken(User user){
     Instant now = Instant.now();

     List<String> authorities =
             user.getRoles()
                     .stream()
                     .map(role -> role.getName())
                     .toList();
     JwtClaimsSet claims =
             JwtClaimsSet.builder()
                     .issuer(issuer)
                     .issuedAt(now)
                     .expiresAt(now.plusSeconds(expiry))
                     .subject(user.getEmail())
                     .claim("authorities", authorities)
                     .claim("name", user.getName())
                     .claim("id", user.getId())
                     .build();

     Jwt jwt = jwtEncoder.encode(JwtEncoderParameters.from(claims));
     return jwt.getTokenValue();
 }
}
