// CursoRepository.java
package com.example.demo.repository;

import com.example.demo.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    List<Curso> findByCicloId(Long cicloId);
    List<Curso> findByPeriodoId(Long periodoId);
}