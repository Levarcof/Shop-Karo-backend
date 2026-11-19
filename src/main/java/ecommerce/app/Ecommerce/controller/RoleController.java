package ecommerce.app.Ecommerce.controller;

import ecommerce.app.Ecommerce.dtos.RoleAddRequest;
import ecommerce.app.Ecommerce.dtos.RoleAddResponse;
import ecommerce.app.Ecommerce.service.RoleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/role")
public class RoleController {
    RoleService roleService ;
    public RoleController(RoleService roleService){
        this.roleService = roleService;
    }
    @PostMapping("/add")
    public ResponseEntity<RoleAddResponse> add(@RequestBody RoleAddRequest roleAddRequest){
        RoleAddResponse roleAddResponse = roleService.add(roleAddRequest);
        return ResponseEntity.ok().body(roleAddResponse);

    }
}
