package com.example.demo.service;

import com.example.demo.model.Aula;
import com.example.demo.repository.AulaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class AulaService {

    private final AulaRepository aulaRepository;

    public AulaService(AulaRepository aulaRepository) {
        this.aulaRepository = aulaRepository;
    }

    @Transactional
    public Aula crear(Aula aula) {
        if (aulaRepository.existsByCodigo(aula.getCodigo()))
            throw new IllegalArgumentException("Ya existe un aula con ese código: " + aula.getCodigo());
        return aulaRepository.save(aula);
    }

    @Transactional(readOnly = true)
    public List<Aula> listar() { 
        return aulaRepository.findAll(); 
    }

    @Transactional(readOnly = true)
    public Aula obtenerPorId(Long id) {
        return aulaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Aula no encontrada con ID: " + id));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!aulaRepository.existsById(id))
            throw new EntityNotFoundException("Aula no encontrada con ID: " + id);
        aulaRepository.deleteById(id);
    }

    // ⭐ AGREGA ESTE MÉTODO: Resuelve el error en tu controlador para las aulas
    @Transactional
    public Aula actualizar(Long id, Aula datos) {
        Aula aula = obtenerPorId(id);
        
        // Copiamos los campos del formulario de edición
        aula.setCodigo(datos.getCodigo());
        aula.setCapacidad(datos.getCapacidad());
        
        // Si tu clase Aula tiene más atributos (ej. pabellon, tipo, etc.), los agregas aquí:
        // aula.setTuAtributo(datos.getTuAtributo());

        return aulaRepository.save(aula);
    }
}