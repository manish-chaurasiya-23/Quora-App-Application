package com.example.QuoraAppApplication.controllers;

import com.example.QuoraAppApplication.dtos.AuthRequestDTO;
import com.example.QuoraAppApplication.dtos.AuthResponse;
import com.example.QuoraAppApplication.services.JwtService;
import com.example.QuoraAppApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Value("${cookie.expiry}")
    private int cookieExpiry;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @PostMapping()
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response){
        System.out.println("Request Received");
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequestDTO.getUsername(), authRequestDTO.getPassword()));
        if(authentication.isAuthenticated()) {

            Map<String, Object> payload = new HashMap<>();
            payload.put("username", authRequestDTO.getUsername());
            String jwtToken = jwtService.createToken(payload, authentication.getPrincipal().toString());

            ResponseCookie cookie = ResponseCookie.from("JwtToken", jwtToken).httpOnly(true).secure(false).maxAge(cookieExpiry).build();
            response.setHeader(HttpHeaders.SET_COOKIE,cookie.toString());
            return new ResponseEntity<>(AuthResponse.builder().username(authRequestDTO.getUsername()).success(true).build(), HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }
}
