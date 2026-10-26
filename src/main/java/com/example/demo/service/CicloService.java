package com.example.demo.service;

import com.example.demo.model.Ciclo;
import com.example.demo.repository.CicloRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CicloService {

    private final CicloRepository cicloRepository;

    public CicloService(CicloRepository cicloRepository) {
        this.cicloRepository = cicloRepository;
    }

    @Transactional(readOnly = true)
    public List<Ciclo> listarTodos() {
        return cicloRepository.findAll();
    }

    @Transactional
    public Ciclo crear(Ciclo ciclo) {
        return cicloRepository.save(ciclo);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!cicloRepository.existsById(id))
            throw new EntityNotFoundException("Ciclo no encontrado con ID: " + id);
        cicloRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Ciclo obtenerPorId(Long id) {
        return cicloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Ciclo no encontrado con ID: " + id));
    }

    // ⭐ ADAPTADO: Ahora usa únicamente el campo 'nombre' que almacena el Enum
    @Transactional
    public Ciclo actualizar(Long id, Ciclo datos) {
        Ciclo ciclo = obtenerPorId(id);
        
        // Dado que tu atributo 'nombre' es de tipo TipoCiclo (Enum), 
        // mapeamos directamente usando los métodos que ya existen en tu Ciclo.java
        ciclo.setNombre(datos.getNombre());
        
        return cicloRepository.save(ciclo);
    }
}