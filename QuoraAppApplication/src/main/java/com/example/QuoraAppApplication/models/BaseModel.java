package com.example.QuoraAppApplication.models;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;


@MappedSuperclass // This annotation is used to specify that the class is an entity class that is a super class for other entity classes
@Getter
@Setter
public class BaseModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // This annotation is used to specify the primary key generation strategy to be used
    private Long id;
}
