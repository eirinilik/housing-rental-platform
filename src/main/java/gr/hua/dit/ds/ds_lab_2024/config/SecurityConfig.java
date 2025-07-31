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
                        .requestMatchers("/", "/home", "/register", "/saveUser", "/images/**", "/js/**", "/css/**").permitAll() // General public access
                        .requestMatchers("/properties").permitAll() // All can view approved properties
                        .requestMatchers("/properties/new").hasRole("OWNER") // Only Owners can add new property
                        .requestMatchers("/properties/my/**").hasRole("OWNER") // Owners can view their properties
                        .requestMatchers("/properties/{propertyId}/apply").hasRole("TENANT") // Tenants can apply
                        .requestMatchers("/tenant/**").hasRole("TENANT") // Tenant specific paths
                        .requestMatchers("/owner/**").hasRole("OWNER") // Owner specific paths
                        .requestMatchers("/admin/**").hasRole("ADMIN") // Admin specific paths
                        // All other authenticated requests
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/properties", true) // Redirect to properties list after successful login
                        .permitAll())
                .logout((logout) -> logout.permitAll());
        return http.build();
    }
}