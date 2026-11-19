package ecommerce.app.Ecommerce.config;

import ecommerce.app.Ecommerce.service.CustomOidcUserService;
import ecommerce.app.Ecommerce.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.AuthenticationManager;


import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.List;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

@Configuration
public class SecurityConfig {
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }



    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder(); //PasswordEncoder is an interface and  BCryptPasswordEncoder is a implemetation class for this interface
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider (CustomUserDetailsService userDetailsService , PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager (DaoAuthenticationProvider daoAuthenticationProvider){
        return new ProviderManager(daoAuthenticationProvider);
    }
       @Bean
    public SecurityFilterChain  securityFilterChain(HttpSecurity httpSecurity , JwtAuthenticationConverter jwtAuthenticationConverter , CustomOidcUserService customOidcUserService , OAuth2SuccessHandler oAuth2SuccessHandler ){
        httpSecurity
                .cors(Customizer.withDefaults())
                .csrf(csrf->csrf.disable())
                .authorizeHttpRequests(auth->
                        auth .
                                requestMatchers("/api/admin/**").hasRole("ADMIN")
                                .requestMatchers("/api/users/register"  , "/api/users/login" ,"/api/users/logout",  "/api/role/add" , "/api/razorpay/webhook" , "/api/users/login", "/api/role/add",
                                        "/api/razorpay/webhook", "/api/users/loginByGoogle", "/api/review/rating-summary/*", "/api/review/*",
                                        "/oauth2/**", "/login/oauth2/**" , "/api/product/**", "/api/category/**" )
                                .permitAll().anyRequest().authenticated()
                )
                .sessionManagement(session->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )
                .oauth2ResourceServer(oauth2->
                        oauth2.jwt(jwt->
                                jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)
                        )
                )
                .oauth2Login(oatuh -> oatuh
                                .userInfoEndpoint(userInfo -> userInfo.oidcUserService(customOidcUserService))
                        .successHandler(oAuth2SuccessHandler)
                        .failureUrl("http://localhost:3000/auth/callback?error=google_failed")
                )
                .logout(logout -> logout
                        .logoutUrl("/api/users/logout")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(200))
                );
        return  httpSecurity.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter(){
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter ();
        authoritiesConverter.setAuthoritiesClaimName(
                "authorities"
        );
        authoritiesConverter.setAuthorityPrefix("");
        JwtAuthenticationConverter authenticationConverter = new JwtAuthenticationConverter();
        authenticationConverter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return authenticationConverter;

    }
    @Bean
    public SecretKey jwtSecretKey(
            @Value("${jwt.secret}") String secret) {

        byte[] decodedKey =
                Base64.getDecoder().decode(secret);

        return new SecretKeySpec(
                decodedKey,
                "HmacSHA256"
        );
    }

    @Bean
    public JwtEncoder jwtEncoder(
            SecretKey secretKey) {

        return NimbusJwtEncoder
                .withSecretKey(secretKey)
                .algorithm(MacAlgorithm.HS256)
                .build();
    }

    @Bean
    public JwtDecoder jwtDecoder(
            SecretKey secretKey,
            @Value("${jwt.issuer}") String issuer) {

        NimbusJwtDecoder decoder =
                NimbusJwtDecoder
                        .withSecretKey(secretKey)
                        .macAlgorithm(MacAlgorithm.HS256)
                        .build();

        decoder.setJwtValidator(
                JwtValidators.createDefaultWithIssuer(
                        issuer
                )
        );

        return decoder;
    }





}
