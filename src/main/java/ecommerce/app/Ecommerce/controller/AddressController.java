package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.AddressDto;
import ecommerce.app.Ecommerce.dtos.AddressReqDto;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.AddressService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/address")
public class AddressController {
    private AddressService addressService;
    private UserRepository userRepository;

    public AddressController(AddressService addressService , UserRepository userRepository) {
        this.addressService = addressService;
        this.userRepository = userRepository;
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

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAllAdresses(Authentication auth){
        return  ResponseEntity.ok(addressService.getAddresses(currentUserId(auth)));

    }

    @PostMapping("/add")
    public ResponseEntity<String>  add(@RequestBody AddressReqDto addressReqDto , Authentication auth){
        return ResponseEntity.ok(addressService.add(addressReqDto , currentUserId(auth)));
    }

    @PatchMapping("/remove/{id}")
    public ResponseEntity<String> remove(@PathVariable Long id , Authentication auth){
        return ResponseEntity.ok(addressService.remove(id , currentUserId(auth)));
    }

    @PatchMapping("/default/{id}")
    public  ResponseEntity<String> setDefault(@PathVariable Long id , Authentication auth){
        return  ResponseEntity.ok(addressService.setDefault(id , currentUserId(auth)));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable Long id,
                                         @RequestBody AddressReqDto dto,
                                         Authentication auth) {
        return ResponseEntity.ok(addressService.update(id, dto, currentUserId(auth)));
    }

}
