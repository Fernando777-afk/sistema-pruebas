package com.example.demo.service;

import com.example.demo.model.*;
import com.example.demo.repository.*;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class MatriculaService {

    private final MatriculaRepository matriculaRepository;
    private final UsuarioRepository usuarioRepository;
    private final CicloRepository cicloRepository;
    private final PeriodoRepository periodoRepository;

    public MatriculaService(MatriculaRepository matriculaRepository,
                            UsuarioRepository usuarioRepository,
                            CicloRepository cicloRepository,
                            PeriodoRepository periodoRepository) {
        this.matriculaRepository = matriculaRepository;
        this.usuarioRepository = usuarioRepository;
        this.cicloRepository = cicloRepository;
        this.periodoRepository = periodoRepository;
    }

    public Matricula matricular(Long estudianteId, Long cicloId, Long periodoId) {
        matriculaRepository.findByEstudianteIdAndPeriodoId(estudianteId, periodoId)
                .ifPresent(m -> { throw new RuntimeException("El estudiante ya está matriculado en este periodo"); });

        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Ciclo ciclo = cicloRepository.findById(cicloId)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado"));
        Periodo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));

        Matricula matricula = new Matricula();
        matricula.setEstudiante(estudiante);
        matricula.setCiclo(ciclo);
        matricula.setPeriodo(periodo);
        matricula.setFechaMatricula(LocalDate.now());
        matricula.setEstado(Matricula.Estado.ACTIVO);

        return matriculaRepository.save(matricula);
    }

    /* ── NUEVO: MÉTODO DE ACTUALIZACIÓN ADAPTADO A TU ARQUITECTURA ── */
    public void actualizar(Long id, Long estudianteId, Long cicloId, Long periodoId) {
        // 1. Buscamos la matrícula existente
        Matricula matricula = obtenerPorId(id);
        
        // 2. Buscamos las nuevas entidades correspondientes a los IDs enviados desde el formulario
        Usuario estudiante = usuarioRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));
        Ciclo ciclo = cicloRepository.findById(cicloId)
                .orElseThrow(() -> new RuntimeException("Ciclo no encontrado"));
        Periodo periodo = periodoRepository.findById(periodoId)
                .orElseThrow(() -> new RuntimeException("Periodo no encontrado"));

        // 3. Actualizamos las relaciones
        matricula.setEstudiante(estudiante);
        matricula.setCiclo(ciclo);
        matricula.setPeriodo(periodo);

        // 4. Guardamos los cambios en la base de datos
        matriculaRepository.save(matricula);
    }

    public List<Matricula> listarPorEstudiante(Long estudianteId) {
        return matriculaRepository.findByEstudianteId(estudianteId);
    }

    public List<Matricula> listarTodas() { return matriculaRepository.findAll(); }

    public Matricula obtenerPorId(Long id) {
        return matriculaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));
    }
}