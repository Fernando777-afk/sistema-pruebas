package com.example.demo.service;

import com.example.demo.model.Periodo;
import com.example.demo.repository.PeriodoRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PeriodoService {

    private final PeriodoRepository periodoRepository;

    public PeriodoService(PeriodoRepository periodoRepository) {
        this.periodoRepository = periodoRepository;
    }

    public Periodo crear(Periodo periodo) { return periodoRepository.save(periodo); }

    public List<Periodo> listar() { return periodoRepository.findAll(); }

    public Periodo obtenerPorId(Long id) {
        return periodoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));
    }

    public Periodo actualizar(Long id, Periodo datos) {
        Periodo p = obtenerPorId(id);
        p.setNombre(datos.getNombre());
        p.setFechaInicio(datos.getFechaInicio());
        p.setFechaFin(datos.getFechaFin());
        p.setEstado(datos.getEstado());
        return periodoRepository.save(p);
    }

    public void eliminar(Long id) {
        if (!periodoRepository.existsById(id))
            throw new RuntimeException("Periodo no encontrado");
        periodoRepository.deleteById(id);
    }
}