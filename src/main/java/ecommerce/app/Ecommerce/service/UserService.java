package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.*;
import ecommerce.app.Ecommerce.entity.Address;
import ecommerce.app.Ecommerce.entity.Role;
import ecommerce.app.Ecommerce.entity.User;
import ecommerce.app.Ecommerce.enums.AddressType;
import ecommerce.app.Ecommerce.repository.RoleRepository;
import ecommerce.app.Ecommerce.repository.UserRepository;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    JwtService jwtService;

    public UserService(UserRepository userRepository , RoleRepository roleRepository , PasswordEncoder passwordEncoder , JwtService jwtService){
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public UserResponseDto register(UserRegisterRequestDto userRegisterRequestDto){
        User user = new User();

         if(userRegisterRequestDto.getAddresses() != null && !userRegisterRequestDto.getAddresses().isEmpty()){
             for(AddressReqDto reqAddress : userRegisterRequestDto.getAddresses()){
                 Address address = getAddress(reqAddress);
                 address.setUser(user);
                 user.getAddresses().add(address);
             }
             user.getAddresses().get(0).setDefault(true);
         }

        user.setEmail(userRegisterRequestDto.getEmail());
        user.setName(userRegisterRequestDto.getName());
        user.setProfileImageUrl(userRegisterRequestDto.getProfileImageUrl());

        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(()->
           new ResourceNotFoundException("Role Not Found While creating the User")
        );

        user.getRoles().add(role);
        String encodedPassword = passwordEncoder.encode(userRegisterRequestDto.getPassword());
        user.setPassword(encodedPassword);
        user.setEnabled(true);
        user.setCreated_at(LocalDateTime.now());
        user.setUpdated_at(LocalDateTime.now());
        user.setProvider(null);
        user.setProviderSubject(null);

       User savedUser = userRepository.save(user);

        UserResponseDto responseDto = regsiterResponseConverter(savedUser);
        return responseDto;
    }

    private Address getAddress(AddressReqDto addressReqDto){
        Address address = new Address();
        address.setCity(addressReqDto.getCity());
        address.setCountry(addressReqDto.getCountry());
        address.setFullName(addressReqDto.getFullName());
        address.setLine1(addressReqDto.getLine1());
        address.setLine2(addressReqDto.getLine2());
        address.setPhone(addressReqDto.getPhone());
        address.setPincode(addressReqDto.getPincode());
        address.setCreatedAt(LocalDateTime.now());
        if ("HOME".equalsIgnoreCase(addressReqDto.getType())) {
            address.setType(AddressType.HOME);
        } else if ("WORK".equalsIgnoreCase(addressReqDto.getType())) {
            address.setType(AddressType.WORK);
        } else {
            address.setType(AddressType.OTHER);
        }
        address.setState(addressReqDto.getState());

        return address;
    }

    public void  registerByGoogle(String provider , OidcUser oidcUser){
        String providerSubject = oidcUser.getSubject();
        Optional<User> existingUser = userRepository.findByProviderAndProviderSubject(provider,providerSubject);
        if(existingUser.isPresent()){
            return;
        }
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(()->new ResourceNotFoundException("Role not found"));
        String name = oidcUser.getClaimAsString("name");
        String email = oidcUser.getClaimAsString("email");
        String profileImage = oidcUser.getClaimAsString("picture");

        User user = new User();
        user.setProviderSubject(providerSubject);
        user.setProvider(provider);
        user.setUpdated_at(LocalDateTime.now());
        user.setCreated_at(LocalDateTime.now());
        user.setProfileImageUrl(profileImage);
        user.setEnabled(true);
        user.setPassword(null);
        user.setName(name);
        user.setEmail(email);
        user.getRoles().add(role);

        userRepository.save(user);
    }
    public UserResponseDto login(UserLoginRequestDto userLoginRequestDto){
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail()).orElseThrow(()-> new ResourceNotFoundException("User Not Found"));
        return regsiterResponseConverter(user);
    }

    private UserResponseDto regsiterResponseConverter(User user) {
        String token =  jwtService.createToken(user);
        List<String> roles = new ArrayList<>();
        for(Role role : user.getRoles()){
            roles.add(role.getName());
        }
        UserResponseDto responseDto = new UserResponseDto(user.getName(),
         user.getProfileImageUrl() , user.getEmail() , user.getCreated_at() , token ,roles);

        return  responseDto;
    }

    public UserResponseDto getResponseForGoogleLogin(String provider , String providerSubject) {
        User user = userRepository.findByProviderAndProviderSubject(provider , providerSubject).orElseThrow(()->new ResourceNotFoundException("User was not store in db"));
        return regsiterResponseConverter(user);
    }

    public void updateUser(UpdateUserDto updateUserDto) {
        User user = userRepository.findByEmail(updateUserDto.getEmail()).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        user.setName(updateUserDto.getName());
        user.setProfileImageUrl(updateUserDto.getProfileImageUrl());
        userRepository.save(user);
    }

}
