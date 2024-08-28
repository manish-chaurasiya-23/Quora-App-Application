package com.example.QuoraAppApplication.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private Boolean success;
    private String username;
}
