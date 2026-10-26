// CicloRepository.java
package com.example.demo.repository;

import com.example.demo.model.Ciclo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CicloRepository extends JpaRepository<Ciclo, Long> {}