package com.example.QuoraAppApplication.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;

import java.util.Set;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Tag extends BaseModel{
    private String name;

    @ManyToMany(mappedBy = "tags")
    private Set<Question> questions;

    @ManyToMany(mappedBy = "followedTags")
    private Set<User> followers;
}