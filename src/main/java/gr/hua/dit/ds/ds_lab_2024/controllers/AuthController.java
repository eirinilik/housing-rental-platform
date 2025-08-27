package gr.hua.dit.ds.ds_lab_2024.controllers;

import gr.hua.dit.ds.ds_lab_2024.entities.Role;
import gr.hua.dit.ds.ds_lab_2024.repositories.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {


    RoleRepository roleRepository;

    public AuthController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

//    @PostConstruct
//    public void setup() {
//        Role role_user = new Role("ROLE_USER");
//        Role role_admin = new Role("ROLE_ADMIN");
//        Role role_owner = new Role("ROLE_OWNER"); // Νέο
//        Role role_tenant = new Role("ROLE_TENANT"); // Νέο
//
//        roleRepository.updateOrInsert(role_user);
//        roleRepository.updateOrInsert(role_admin);
//        roleRepository.updateOrInsert(role_owner); // Νέο
//        roleRepository.updateOrInsert(role_tenant); // Νέο
//    }

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
}
