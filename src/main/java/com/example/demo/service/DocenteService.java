package com.example.demo.service;

import com.example.demo.model.Docente;
import com.example.demo.repository.DocenteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class DocenteService {

    private final DocenteRepository docenteRepository;

    public DocenteService(DocenteRepository docenteRepository) {
        this.docenteRepository = docenteRepository;
    }

    @Transactional
    public Docente registrar(Docente docente) {
        if (docenteRepository.existsByDni(docente.getDni())) {
            throw new IllegalArgumentException("Ya existe un docente con el DNI: " + docente.getDni());
        }
        docente.setEstado(Docente.Estado.ACTIVO);
        return docenteRepository.save(docente);
    }

    @Transactional(readOnly = true)
    public List<Docente> listar() { 
        return docenteRepository.findAll(); 
    }

    @Transactional(readOnly = true)
    public Docente obtenerPorId(Long id) {
        return docenteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Docente no encontrado con ID: " + id));
    }

    @Transactional
    public Docente actualizar(Long id, Docente datos) {
        Docente d = obtenerPorId(id);
        
        d.setNombre(datos.getNombre());
        d.setApellido(datos.getApellido());
        d.setEspecialidad(datos.getEspecialidad());
        
        if (datos.getEstado() != null) {
            d.setEstado(datos.getEstado());
        }
        
        return docenteRepository.save(d);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!docenteRepository.existsById(id))
            throw new RuntimeException("Docente no encontrado");
        docenteRepository.deleteById(id);
    }
}