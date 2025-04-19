package com.crm.services;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.crm.models.User;
import com.crm.repositories.UserRepository;


@Service
public class UserInfoService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User userDetail = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));// Assuming 'email' is used as username

        // Converting User to UserDetails
        return new org.springframework.security.core.userdetails.User(userDetail.getUsername(), userDetail.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority(userDetail.getType())));
//        		userDetail.map(UserInfoDetails::new)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }

    public String addUser(User user) {
        // Encode password before saving the user
        user.setPassword(encoder.encode(user.getPassword()));	
        userRepository.save(user);
        return "User Added Successfully";
    }
}
