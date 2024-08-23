package com.example.QuoraAppApplication.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class TagDTO {
    private Long id;
    private String name;
}
