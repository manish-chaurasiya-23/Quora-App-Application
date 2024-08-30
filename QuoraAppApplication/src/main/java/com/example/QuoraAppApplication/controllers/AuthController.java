package com.example.QuoraAppApplication.controllers;

import com.example.QuoraAppApplication.dtos.AuthRequestDTO;
import com.example.QuoraAppApplication.dtos.AuthResponse;
import com.example.QuoraAppApplication.dtos.UserDTO;
import com.example.QuoraAppApplication.models.User;
import com.example.QuoraAppApplication.services.JwtService;
import com.example.QuoraAppApplication.services.UserDetailsServiceImplementation;
import com.example.QuoraAppApplication.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1/auth/")
public class AuthController {

    @Value("${cookie.expiry}")
    private int cookieExpiry;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    @PostMapping("/signup")
    public User createUser(@RequestBody UserDTO userDTO) {
        return userService.createUser(userDTO);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody AuthRequestDTO authRequestDTO, HttpServletResponse response){

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

    @GetMapping("/validate")
    public ResponseEntity<?> validate(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals("JwtToken")) {
                    token = cookie.getValue();
                }
            }
        }
        if (token == null) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        String email = jwtService.extractEmail(token);
        if (email != null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            if (jwtService.validateToken(token, userDetails.getUsername())) {
                return new ResponseEntity<>("Success", HttpStatus.OK);
            }
        }
        return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
    }
}
