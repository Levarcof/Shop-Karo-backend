package ecommerce.app.Ecommerce.dtos;

import java.time.LocalDateTime;
import java.util.List;


public class UserResponseDto {
    private String name;
    private  String profileImageUrl;
    private String email;
    private  LocalDateTime created_at;
    private String token;
    private List<String> role ;

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }

    public UserResponseDto(String name , String profileImageUrl , String email , LocalDateTime created_at , String token , List<String> role){
        this.created_at = created_at;
        this.email = email;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.token = token;
        this.role = role;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDateTime getCreated_at() {
        return created_at;
    }

    public void setCreated_at(LocalDateTime created_at) {
        this.created_at = created_at;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
