package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.CartDetaiilsDto;
import ecommerce.app.Ecommerce.dtos.CartReqDto;
import ecommerce.app.Ecommerce.dtos.CartResponseDto;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.CartService;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private CartService cartService;
    private UserRepository userRepository;

    public CartController(CartService cartService , UserRepository userRepository)  {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/add")
    public ResponseEntity<CartResponseDto> add(@RequestBody CartReqDto cartReqDto , Authentication auth){
        CartResponseDto cartResponseDto = cartService.add(cartReqDto , currentUserId(auth));
        return ResponseEntity.ok().body(cartResponseDto);

    }

    @DeleteMapping("/delete")
    public ResponseEntity<CartResponseDto> delete(@RequestBody CartReqDto cartReqDto , Authentication auth){
        CartResponseDto cartResponseDto = cartService.delete(cartReqDto , currentUserId(auth));
        return ResponseEntity.ok().body(cartResponseDto);
    }

    @GetMapping
    public ResponseEntity<CartDetaiilsDto> getCart(Authentication auth){
        return  ResponseEntity.ok(cartService.getCart(currentUserId(auth)));
    }

    private Long currentUserId(Authentication auth){
        if (auth instanceof JwtAuthenticationToken jwtAuth) {
            String email = jwtAuth.getToken().getSubject();

            return userRepository.findByEmail(email)
                    .orElseThrow(() ->
                            new ResourceNotFoundException("User not found")
                    )
                    .getId();
        }

        throw new IllegalStateException("JWT authentication required");
    }


}
