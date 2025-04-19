package com.crm.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.crm.models.User;
import com.crm.repositories.UserRepository;

import java.time.LocalDate;
import java.util.Optional;

@Configuration
public class SuperAdminInitializer {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Bean
    public ApplicationRunner initializeSuperAdmin() {
        return args -> {
            String superAdminUsername= "superadmin@gmail.com";
            String superAdminPassword = "1234";
            
            Optional<User> existingSuperAdmin = userRepository.findByUsername(superAdminUsername);
            
            if (existingSuperAdmin.isEmpty()) {
            	System.err.println("SUPER ADMIN NOT FOUND");
                User superAdmin = new User();
                superAdmin.setName("Swapnil");
                superAdmin.setUsername(superAdminUsername);
                superAdmin.setPassword(passwordEncoder.encode(superAdminPassword));
                superAdmin.setCompanyName("SUPER_ADMIN");
                superAdmin.setType("SUPER_ADMIN");
                superAdmin.setLocation("india");
                superAdmin.setStatus(1);
                superAdmin.setCreatedDate(LocalDate.now());
                superAdmin.setModifiedDate(LocalDate.now());
                
                userRepository.save(superAdmin);
                
                System.out.println("✅ SUPER_ADMIN created successfully!");
                System.out.println("username : "+superAdminUsername);
                System.out.println("password : "+superAdminPassword);

            } else {
                System.out.println("✅ SUPER_ADMIN already exists!");
                System.out.println("username : "+superAdminUsername);
                System.out.println("password : "+superAdminPassword);                
            }
        };
    }
}

