// PagoRepository.java
package com.example.demo.repository;

import com.example.demo.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PagoRepository extends JpaRepository<Pago, Long> {
    List<Pago> findByMatriculaId(Long matriculaId);
    List<Pago> findByEstado(Pago.Estado estado);
}