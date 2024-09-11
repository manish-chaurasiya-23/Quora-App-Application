package com.example.QuoraAppApplication.filters;

import com.example.QuoraAppApplication.services.JwtService;
import com.example.QuoraAppApplication.services.UserDetailsServiceImplementation;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsServiceImplementation userDetailsService;

    private final JwtService jwtService;

    public JwtAuthFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(@Nonnull  HttpServletRequest request, @Nonnull HttpServletResponse response, @Nonnull FilterChain filterChain) throws ServletException, IOException {
        // fetching the Authorization key value from the Headers of the request that hold the JWT Token with prefix "Bearer "
        final String token = request.getHeader("Authorization");
        try {
            if (token == null || !token.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                return;
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
            System.out.println("Token expired: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token expired");

        } catch (UsernameNotFoundException e) {
            System.out.println("User not found for email: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("User not found");

        } catch (Exception e) {
            System.out.println("Exception occurred: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("An error occurred");

        } finally {
            filterChain.doFilter(request, response);
        }
    }

    @Override
    protected boolean shouldNotFilter(@Nonnull HttpServletRequest request) {
        RequestMatcher validateGETMatcher = new AntPathRequestMatcher("/api/v1/auth/**", HttpMethod.GET.name());
        RequestMatcher validatePOSTMatcher = new AntPathRequestMatcher("/api/v1/auth/**", HttpMethod.POST.name());
        return validateGETMatcher.matches(request) || validatePOSTMatcher.matches(request);
    }
}