package com.example.QuoraAppApplication.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Builder
@Getter
@Setter
public class QuestionDTO {
    private Long id;
    private String title;
    private String content;
    private Long userId;
    private Set<Long> tagIds;
}
