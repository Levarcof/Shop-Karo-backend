package ecommerce.app.Ecommerce.service;

import ecommerce.app.Ecommerce.dtos.RoleAddRequest;
import ecommerce.app.Ecommerce.dtos.RoleAddResponse;
import ecommerce.app.Ecommerce.entity.Role;
import ecommerce.app.Ecommerce.repository.RoleRepository;
import org.springframework.stereotype.Service;

@Service
public class RoleService {
    private RoleRepository roleRepository;
    public RoleService(RoleRepository roleRepository){
        this.roleRepository = roleRepository;
    }
    public RoleAddResponse add(RoleAddRequest roleAddRequest) {
        Role role = new Role();
        role.setName(roleAddRequest.getName());
        roleRepository.save(role);
        return new RoleAddResponse(role.getName() , "Role added successfully");
    }
}
