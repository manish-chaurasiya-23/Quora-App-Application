package com.example.QuoraAppApplication.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class AnswerDTO {
    private Long id;
    private String content;
    private Long questionId;
    private Long userId;
}