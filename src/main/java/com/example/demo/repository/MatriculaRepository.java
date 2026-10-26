// MatriculaRepository.java
package com.example.demo.repository;

import com.example.demo.model.Matricula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MatriculaRepository extends JpaRepository<Matricula, Long> {
    List<Matricula> findByEstudianteId(Long estudianteId);
    Optional<Matricula> findByEstudianteIdAndPeriodoId(Long estudianteId, Long periodoId);
}