package ecommerce.app.Ecommerce.dtos;

import ecommerce.app.Ecommerce.entity.Address;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class UserRegisterRequestDto {
    private String name;
    private String password;
    private  String profileImageUrl;
    private String email;

    private List<AddressReqDto> addresses = new ArrayList<>();

    public List<AddressReqDto> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<AddressReqDto> addresses) {
        this.addresses = addresses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
