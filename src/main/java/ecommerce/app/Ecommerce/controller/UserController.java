package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.UpdateUserDto;
import ecommerce.app.Ecommerce.dtos.UserLoginRequestDto;
import ecommerce.app.Ecommerce.dtos.UserRegisterRequestDto;
import ecommerce.app.Ecommerce.dtos.UserResponseDto;
import ecommerce.app.Ecommerce.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    UserService userService;
    AuthenticationManager authenticationManager;
    public  UserController(UserService userService , AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @GetMapping("/health")
    public ResponseEntity<String> health(){
        return ResponseEntity.ok("Yepp it's working");
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> register(@RequestBody UserRegisterRequestDto userRegisterRequestDto){
        UserResponseDto responseDto = userService.register(userRegisterRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDto> login(@RequestBody UserLoginRequestDto userLoginRequestDto){
        Authentication authenticationRequest = UsernamePasswordAuthenticationToken.unauthenticated(userLoginRequestDto.getEmail() , userLoginRequestDto.getPassword());
        Authentication authentication = authenticationManager.authenticate(authenticationRequest);

        UserResponseDto userResponseDto = userService.login(userLoginRequestDto);
        return  ResponseEntity.ok().body(userResponseDto);
    }

    @GetMapping("/loginByGoogle")
    public  ResponseEntity<UserResponseDto> registerByGoogle(@AuthenticationPrincipal OidcUser oidcUser){
    UserResponseDto responseDto = userService.getResponseForGoogleLogin("google", oidcUser.getSubject());
    return ResponseEntity.ok().body(responseDto);
    }

    @PutMapping("/update")
    public ResponseEntity<Void> updateUser(@RequestBody UpdateUserDto updateUserDto){
       userService.updateUser(updateUserDto);
        return ResponseEntity.ok().build();

    }
}
