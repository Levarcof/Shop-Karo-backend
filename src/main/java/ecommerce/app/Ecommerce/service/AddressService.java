package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.AddressDto;
import ecommerce.app.Ecommerce.dtos.AddressReqDto;
import ecommerce.app.Ecommerce.entity.Address;
import ecommerce.app.Ecommerce.entity.User;
import ecommerce.app.Ecommerce.enums.AddressType;
import ecommerce.app.Ecommerce.exception.ResourceNotFoundException;
import ecommerce.app.Ecommerce.repository.AddressRepository;
import ecommerce.app.Ecommerce.repository.UserRepository;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class AddressService {
   private AddressRepository addressRepository;
   private UserRepository userRepository;




    public AddressService(AddressRepository addressRepository , UserRepository userRepository) {
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }


    public List<AddressDto> getAddresses(Long id) {
        List<Address> addresses = addressRepository.findByUserIdAndDeletedFalse(id);
        return addresses.stream()
                .map(this::getAddressDto)
                .toList();
    }

    @Transactional
    public  String add(AddressReqDto addressReqDto , Long userId) {
       User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found"));
       Address address = getAddress(addressReqDto);
       if(user.getAddresses() == null || user.getAddresses().isEmpty()){
           address.setDefault(true);
       }
       address.setUser(user);
       user.getAddresses().add(address);
       userRepository.save(user);
       return "Address added successfully";
    }

    @Transactional
    public  String remove(Long id , Long userId) {
        Address address = addressRepository.findByIdAndUser_IdAndDeletedFalse(id ,userId ).orElseThrow(()->new ResourceNotFoundException("Address not found"));
        if(address.isDeleted()){
            throw new ResourceNotFoundException("Address not found");
        }

        address.setDeleted(true);
        return "Address Deleted Successfully";
    }

    @Transactional
    public String setDefault(Long id , Long userId) {
        Address address = addressRepository.findByIdAndUser_IdAndDeletedFalse(id , userId).orElseThrow(()-> new ResourceNotFoundException("Address not found"));
        addressRepository.clearDefaultAddresses(userId);
        address.setDefault(true);
        return  "Default address successfull";
    }


    private Address getAddress(AddressReqDto addressReqDto){
        Address address = new Address();
        address.setState(addressReqDto.getState());
        address.setPincode(addressReqDto.getPincode());
        if ("HOME".equalsIgnoreCase(addressReqDto.getType())) {
            address.setType(AddressType.HOME);
        } else if ("WORK".equalsIgnoreCase(addressReqDto.getType())) {
            address.setType(AddressType.WORK);
        } else {
            address.setType(AddressType.OTHER);
        }
        address.setPhone(addressReqDto.getPhone());
        address.setCity(addressReqDto.getCity());
        address.setCountry(addressReqDto.getCountry());
        address.setLine1(addressReqDto.getLine1());
        address.setLine2(addressReqDto.getLine2());
        address.setFullName(addressReqDto.getFullName());
        address.setCreatedAt(LocalDateTime.now());

        return address;
    }

    private AddressDto getAddressDto(Address address){
        AddressDto addressDto = new AddressDto(
                address.getId() , address.getFullName() , address.getPhone() , address.getLine1() , address.getLine2() , address.getCity() , address.getState() ,
                address.getPincode() , address.getCountry() , address.getType().name() , address.isDefault()
        );
        return addressDto;
    }


    @Transactional
    public  String update(Long id, AddressReqDto dto, Long userId) {
        if(dto == null){
            throw new IllegalArgumentException("Address data is required");
        }
        Address address = addressRepository.findByIdAndUser_IdAndDeletedFalse(id , userId).orElseThrow(()->new ResourceNotFoundException("Address not found"));
        address.setPhone(dto.getPhone());
        address.setFullName(dto.getFullName());
        address.setLine2(dto.getLine2());
        address.setLine1(dto.getLine1());
        address.setCity(dto.getCity());
        address.setPincode(dto.getPincode());
        address.setCountry(dto.getCountry());
        address.setState(dto.getState());

        String type = dto.getType();
        if(type == null || type.isBlank()){
            throw  new IllegalArgumentException("Adress type is required");
        }

        try{
            address.setType(AddressType.valueOf(type.trim().toUpperCase(Locale.ROOT)));
        }
        catch (IllegalArgumentException ex){
            throw  new IllegalArgumentException("Adress type must be HOME , WORK , OTHER");

        }
        return "Adress update successfully";
    }
}
