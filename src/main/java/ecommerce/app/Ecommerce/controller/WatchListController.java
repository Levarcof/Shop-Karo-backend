package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.WatchListDetailDto;
import ecommerce.app.Ecommerce.dtos.WatchListReqDto;
import ecommerce.app.Ecommerce.dtos.WatchListResponseDto;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.service.WatchListService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/watchlist")
public class WatchListController {
    private WatchListService watchListService;
    private UserRepository userRepository;

    public WatchListController(WatchListService watchListService, UserRepository userRepository) {
        this.watchListService = watchListService;
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
    public ResponseEntity<WatchListDetailDto> getWatchList(Authentication auth){
        return ResponseEntity.ok(watchListService.getWatchList(currentUserId(auth)));
    }

    @PostMapping("/add")
    public ResponseEntity<WatchListResponseDto> add(@RequestBody WatchListReqDto watchListReqDto , Authentication auth){
        return ResponseEntity.ok(watchListService.add(watchListReqDto , currentUserId(auth)));
    }

    @DeleteMapping("/delete")
    public ResponseEntity<WatchListResponseDto> delete(@RequestBody WatchListReqDto watchListReqDto , Authentication auth){
        return  ResponseEntity.ok(watchListService.delete(watchListReqDto , currentUserId(auth)));
    }

}
