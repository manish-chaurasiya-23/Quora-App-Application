package com.example.QuoraAppApplication.services;

import com.example.QuoraAppApplication.helpers.AuthUserDetails;
import com.example.QuoraAppApplication.models.User;
import com.example.QuoraAppApplication.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

// This class is responsible for loading the user in the form of UserDetails Object for auth.

@Service
public class UserDetailsServiceImplementation implements UserDetailsService {

    @Autowired
    private  UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Username is : "+ username);
        Optional<User> user = userRepository.findUserByUsername(username);
        if(user.isPresent()) {
            return new AuthUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("Cannot find the user by the given email");
        }
    }
}
