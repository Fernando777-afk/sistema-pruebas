package com.example.demo.service;

import com.example.demo.model.Matricula;
import com.example.demo.model.Pago;
import com.example.demo.repository.MatriculaRepository;
import com.example.demo.repository.PagoRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class PagoService {

    private final PagoRepository pagoRepository;
    private final MatriculaRepository matriculaRepository;

    public PagoService(PagoRepository pagoRepository, MatriculaRepository matriculaRepository) {
        this.pagoRepository = pagoRepository;
        this.matriculaRepository = matriculaRepository;
    }

    public Pago registrarPago(Long matriculaId, Pago pago) {
        Matricula matricula = matriculaRepository.findById(matriculaId)
                .orElseThrow(() -> new RuntimeException("Matrícula no encontrada"));
        pago.setMatricula(matricula);
        pago.setFechaPago(LocalDate.now());
        pago.setEstado(Pago.Estado.PAGADO);
        return pagoRepository.save(pago);
    }

    /* ── NUEVO: MÉTODO PARA OBTENER UN PAGO POR ID ── */
    public Pago obtenerPorId(Long id) {
        return pagoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + id));
    }

    /* ── NUEVO: MÉTODO PARA ACTUALIZAR CON CONTROL DE CAMPOS ── */
    public void actualizar(Long id, Pago pagoActualizado) {
        // 1. Buscamos el registro real de la base de datos
        Pago pagoExistente = obtenerPorId(id);
        
        // 2. Modificamos únicamente los datos que vienen desde el formulario
        pagoExistente.setMonto(pagoActualizado.getMonto());
        pagoExistente.setFechaPago(pagoActualizado.getFechaPago());
        pagoExistente.setMetodoPago(pagoActualizado.getMetodoPago());
        pagoExistente.setObservacion(pagoActualizado.getObservacion());
        
        // De esta manera conservamos la 'matricula' y el 'estado' originales 
        // evitando que se queden en nulo o causen un error de restricción.

        // 3. Guardamos la entidad mezclada
        pagoRepository.save(pagoExistente);
    }

    public List<Pago> listarPorMatricula(Long matriculaId) {
        return pagoRepository.findByMatriculaId(matriculaId);
    }

    public List<Pago> listarTodos() { return pagoRepository.findAll(); }
}