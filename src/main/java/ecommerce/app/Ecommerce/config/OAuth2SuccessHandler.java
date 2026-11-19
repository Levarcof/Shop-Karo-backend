package ecommerce.app.Ecommerce.config;
import java.io.IOException;

import ecommerce.app.Ecommerce.dtos.UserResponseDto;
import ecommerce.app.Ecommerce.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private static final String FRONTEND_CALLBACK = "http://localhost:3000/auth/callback";

    private final UserService userService;
    private final ObjectMapper objectMapper;

    public OAuth2SuccessHandler(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OidcUser oidcUser = (OidcUser) authentication.getPrincipal();
            UserResponseDto dto =
                    userService.getResponseForGoogleLogin("google", oidcUser.getSubject());

            String json = objectMapper.writeValueAsString(dto);
            String encoded = Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(json.getBytes(StandardCharsets.UTF_8));

//            String: {"name":"Rahul"}
//          ↓ UTF-8
//            Bytes:  [123, 34, 110, 97, 109, 101, ...]  ab esko fir encodeToString  se encoded string me convert kiya jayega.
            // getUrlEncoder  ek object return karta hai , withoutPadding ye encoded string ke last me aane vali padding jese == ko remove larta hai.


            response.sendRedirect(FRONTEND_CALLBACK + "#data=" + encoded);
        } catch (Exception e) {
            response.sendRedirect(FRONTEND_CALLBACK + "?error="
                    + URLEncoder.encode("Google login failed", StandardCharsets.UTF_8));
        }
    }
}
