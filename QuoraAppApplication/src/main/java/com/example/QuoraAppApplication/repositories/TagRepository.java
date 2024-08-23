package com.example.QuoraAppApplication.repositories;

import com.example.QuoraAppApplication.models.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
