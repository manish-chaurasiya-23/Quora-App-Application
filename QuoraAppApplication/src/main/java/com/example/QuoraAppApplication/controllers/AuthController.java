package com.example.QuoraAppApplication.controllers;

import com.example.QuoraAppApplication.dtos.AuthRequestDTO;
import com.example.QuoraAppApplication.dtos.AuthResponse;
import com.example.QuoraAppApplication.dtos.UserDTO;
import com.example.QuoraAppApplication.models.User;
import com.example.QuoraAppApplication.services.JwtService;
import com.example.QuoraAppApplication.services.UserDetailsServiceImplementation;
import com.example.QuoraAppApplication.services.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
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
            String jwtToken = jwtService.createToken(payload, authRequestDTO.getUsername());
            return new ResponseEntity<>(AuthResponse.builder().username(authRequestDTO.getUsername()).success(true).jwtToken(jwtToken).build(), HttpStatus.OK);
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

    @GetMapping("/validate")
    public ResponseEntity<?> validate(@Nonnull HttpServletRequest request) {
        final String token = request.getHeader("Authorization");
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                return new ResponseEntity<>("Invalid Request", HttpStatus.BAD_REQUEST);
            }

            // Remove the "Bearer " prefix from the token
            String actualToken = token.substring(7);

            // Extract the email from the token
            String email = jwtService.extractEmail(actualToken);
            System.out.println("Incoming Email: " + email);

            // Proceed if the email is not null
            if(email != null) {
                // Load user details based on the extracted email
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                // Validate the token using the extracted email
                if (jwtService.validateToken(actualToken, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, null);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the context
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            }
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("Token Expired", HttpStatus.UNAUTHORIZED);

        } catch (UsernameNotFoundException e) {
            return new ResponseEntity<>("User not found", HttpStatus.UNAUTHORIZED);

        } catch (Exception e) {
            return new ResponseEntity<>("An error occurred", HttpStatus.INTERNAL_SERVER_ERROR);

        }
        return new ResponseEntity<>("Authorized", HttpStatus.OK);
    }
}
