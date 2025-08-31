package gr.hua.dit.ds.ds_lab_2024.config;

import gr.hua.dit.ds.ds_lab_2024.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {

    private UserService userService;
    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder passwordEncoder;

    public SecurityConfig(UserService userService, UserDetailsService userDetailsService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        .requestMatchers("/", "/home", "/register", "/saveUser", "/images/**", "/js/**", "/css/**").permitAll() //ΠΡΟΣΒΑΣΗ ΑΠΟ ΟΛΟΥΣ
                        .requestMatchers("/properties").permitAll() //ΠΡΟΣΒΑΣΗ ΑΠΟ ΟΛΟΥΣ
                        .requestMatchers("/properties/new").hasRole("OWNER") // ΜΟΝΟ ΟΙ ΙΔΙΟΚΤΗΤΕΣ ΜΠΟΡΟΥΝ ΝΑ ΠΡΟΣΘΕΣΟΥΝ ΚΑΙΝΟΥΡΙΟ ΑΚΙΝΗΤΟ
                        .requestMatchers("/properties/my/**").hasRole("OWNER") // ΙΔΙΟΚΤΗΤΕΣ ΜΠΟΡΟΥΝ ΝΑ ΔΟΥΝ ΤΑ ΑΚΙΝΗΤΑ ΤΟΥΣ
                        .requestMatchers("/properties/{propertyId}/apply").hasRole("TENANT") // ΕΝΟΙΚΙΑΣΤΕΣ ΜΠΟΡΟΥΝ ΝΑ ΚΑΝΟΥΝ APPLY
                        .requestMatchers("/tenant/**").hasRole("TENANT") // ΕΝΟΙΚΙΑΣΤΗ specific paths
                        .requestMatchers("/owner/**").hasRole("OWNER") // ΙΔΙΟΚΤΗΤΗ specific paths
                        .requestMatchers("/admin/**").hasRole("ADMIN") // ADMIN specific paths
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/properties", true) // ANAKAΤΕΥΘΥΝΣΗ ΣΤΟ ΛΙΣΤΑ ΜΕ ΤΑ ΑΚΙΝΗΤΑ ΜΕΤΑ ΤΟ LOG IN
                        .permitAll())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
}