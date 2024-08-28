package com.example.QuoraAppApplication.controllers;

import com.example.QuoraAppApplication.dtos.AuthRequestDTO;
import com.example.QuoraAppApplication.services.JwtService;
import com.example.QuoraAppApplication.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping()
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDTO authRequestDTO){
        System.out.println("Request Received");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("username",authRequestDTO.getUsername());
            String jwtToken = jwtService.CreateToken(payload,authentication.getPrincipal().toString());
            return  new ResponseEntity<>(jwtToken, HttpStatus.OK);
        } else {
           throw  new UsernameNotFoundException("user not found");
        }
    }
}
