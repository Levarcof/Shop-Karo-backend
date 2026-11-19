package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.entity.CustomUserDetails;
import ecommerce.app.Ecommerce.entity.User;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository){
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if(user.isEmpty()){
            throw   new ResourceNotFoundException("User not found");
        }
        return new CustomUserDetails(user.get());
    }
}
