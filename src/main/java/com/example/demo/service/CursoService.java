package com.example.demo.service;

import com.example.demo.model.Curso;
import com.example.demo.repository.CursoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class CursoService {

    private final CursoRepository cursoRepository;

    public CursoService(CursoRepository cursoRepository) {
        this.cursoRepository = cursoRepository;
    }

    @Transactional
    public Curso crear(Curso curso) { 
        return cursoRepository.save(curso); 
    }

    @Transactional(readOnly = true)
    public List<Curso> listar() { 
        return cursoRepository.findAll(); 
    }

    @Transactional(readOnly = true)
    public List<Curso> listarPorCiclo(Long cicloId) {
        return cursoRepository.findByCicloId(cicloId);
    }

    @Transactional(readOnly = true)
    public Curso obtenerPorId(Long id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Curso no encontrado con ID: " + id));
    }

    // ⭐ MEJORADO: Ahora guarda todas las relaciones necesarias que maneja tu controlador
    @Transactional
    public Curso actualizar(Long id, Curso datos) {
        Curso c = obtenerPorId(id);
        
        c.setNombre(datos.getNombre());
        c.setDescripcion(datos.getDescripcion());
        c.setDocente(datos.getDocente());
        c.setAula(datos.getAula());
        
        // Relaciones clave añadidas para que no se pierdan al editar el curso:
        c.setCiclo(datos.getCiclo());
        c.setPeriodo(datos.getPeriodo());
        
        return cursoRepository.save(c);
    }

    @Transactional
    public void eliminar(Long id) {
        if (!cursoRepository.existsById(id))
            throw new EntityNotFoundException("Curso no encontrado con ID: " + id);
        cursoRepository.deleteById(id);
    }
}