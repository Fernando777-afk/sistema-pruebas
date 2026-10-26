// AulaRepository.java
package com.example.demo.repository;

import com.example.demo.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    boolean existsByCodigo(String codigo);
}