package gr.hua.dit.ds.ds_lab_2024.entities;


import gr.hua.dit.ds.ds_lab_2024.entities.Property;
import gr.hua.dit.ds.ds_lab_2024.entities.PropertyType;
import gr.hua.dit.ds.ds_lab_2024.entities.Role;
import gr.hua.dit.ds.ds_lab_2024.entities.User;
import gr.hua.dit.ds.ds_lab_2024.repositories.PropertyRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.RoleRepository;
import gr.hua.dit.ds.ds_lab_2024.repositories.UserRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Set;

@Configuration
public class InitialData {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final PropertyRepository propertyRepository;

    public InitialData(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       PropertyRepository propertyRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.propertyRepository = propertyRepository;
    }

    @Transactional
    @PostConstruct
    public void populateDBWithInitialData() {
        // Δημιουργία Ρόλων
        Role roleAdmin = new Role("ROLE_ADMIN");
        Role roleOwner = new Role("ROLE_OWNER");
        Role roleTenant = new Role("ROLE_TENANT");
        Role roleUser = new Role("ROLE_USER");

        roleAdmin = this.roleRepository.updateOrInsert(roleAdmin);
        roleOwner = this.roleRepository.updateOrInsert(roleOwner);
        roleTenant = this.roleRepository.updateOrInsert(roleTenant);
        roleUser = this.roleRepository.updateOrInsert(roleUser);

        // Δημιουργία αρχικού Admin
        if (this.userRepository.findByUsername("Admin").isEmpty()) {
            User userAdmin = new User("Admin", "admin@gmail.com", passwordEncoder.encode("123"));
            userAdmin.setRoles(Set.of(roleAdmin));
            this.userRepository.save(userAdmin);
        }

        // Δημιουργία αρχικού Owner
        if (this.userRepository.findByUsername("Dimitris Spurou").isEmpty()) {
            User userOwner = new User("Dimitris Spurou", "dimitris@gmail.com", passwordEncoder.encode("123"));
            userOwner.setRoles(Set.of(roleOwner));
            this.userRepository.save(userOwner);
        }

        // Δημιουργία αρχικού Tenant
        if (this.userRepository.findByUsername("Afroditi Mamoy").isEmpty()) {
            User userTenant = new User("Afroditi Mamoy", "afroditi@gmail.com", passwordEncoder.encode("123"));
            userTenant.setRoles(Set.of(roleTenant));
            this.userRepository.save(userTenant);
        }

        // Δημιουργία αρχικού Property για επίδειξη
        if (this.propertyRepository.findByIsApprovedTrue().isEmpty()) {
            User owner = this.userRepository.findByUsername("Dimitris Spurou").orElseThrow();
            Property property = new Property(
                    "Μονοκατοικία στο κέντρο",
                    "μια oμορφη μονοκατοικία με κηπο",
                    "Οδός Ακινήτων 10",
                    PropertyType.HOUSE,
                    3,
                    2,
                    120,
                    850.00,
                    LocalDate.now(),
                    owner
            );
            property.setIsApproved(true);
            this.propertyRepository.save(property);
        }
    }
}
