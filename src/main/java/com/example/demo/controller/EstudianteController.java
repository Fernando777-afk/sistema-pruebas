package com.example.demo.controller;

import com.example.demo.model.Curso;
import com.example.demo.model.Matricula;
import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import com.example.demo.service.MatriculaService;
import com.example.demo.service.PagoService;
import com.example.demo.service.CursoService; // 🔥 Importación añadida
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/estudiante")
@PreAuthorize("hasRole('ESTUDIANTE')")
public class EstudianteController {

    private final UsuarioRepository usuarioRepository;
    private final MatriculaService matriculaService;
    private final PagoService pagoService;
    private final CursoService cursoService; // 🔥 Inyección añadida para listar cursos

    public EstudianteController(UsuarioRepository usuarioRepository,
                                MatriculaService matriculaService,
                                PagoService pagoService,
                                CursoService cursoService) { // 🔥 Agregado al constructor
        this.usuarioRepository = usuarioRepository;
        this.matriculaService = matriculaService;
        this.pagoService = pagoService;
        this.cursoService = cursoService;
    }

    private Usuario getUsuarioActual(Authentication auth) {
        return usuarioRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    // ── DASHBOARD ──
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth, Model model) {
        Usuario u = getUsuarioActual(auth);
        model.addAttribute("usuario", u);
        model.addAttribute("matriculas", matriculaService.listarPorEstudiante(u.getId()));
        return "estudiante/dashboard";
    }

    // ── MIS CURSOS (MÉTODO CORREGIDO) ──
    @GetMapping("/mis-cursos")
    public String misCursos(Authentication auth, Model model) {
        Usuario u = getUsuarioActual(auth);
        
        List<Matricula> matriculas = matriculaService.listarPorEstudiante(u.getId());
        model.addAttribute("matriculas", matriculas);
        
        // 🔥 LÓGICA CLAVE: Si el estudiante está matriculado, obtenemos las asignaturas de su ciclo
        if (!matriculas.isEmpty()) {
            // Tomamos la primera matrícula activa (o la más reciente)
            Matricula matriculaActiva = matriculas.get(0);
            
            // Obtenemos los cursos asociados al ciclo de esa matrícula
            List<Curso> misCursos = cursoService.listarPorCiclo(matriculaActiva.getCiclo().getId());
            model.addAttribute("cursos", misCursos);
        }
        
        return "estudiante/mis-cursos";
    }

    // ── MIS PAGOS ──
    @GetMapping("/mis-pagos")
    public String misPagos(Authentication auth, Model model) {
        Usuario u = getUsuarioActual(auth);
        List<Matricula> matriculas = matriculaService.listarPorEstudiante(u.getId());
        model.addAttribute("matriculas", matriculas);
        
        // Pagos de cada matrícula
        if (!matriculas.isEmpty()) {
            model.addAttribute("pagos", pagoService.listarPorMatricula(matriculas.get(0).getId()));
        }
        return "estudiante/mis-pagos";
    }

    // ── DETALLE PAGOS POR MATRÍCULA ──
    @GetMapping("/mis-pagos/{matriculaId}")
    public String detallePagos(@PathVariable Long matriculaId, Model model) {
        model.addAttribute("pagos", pagoService.listarPorMatricula(matriculaId));
        model.addAttribute("matricula", matriculaService.obtenerPorId(matriculaId));
        return "estudiante/detalle-pagos";
    }
}