package com.example.demo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import com.example.demo.model.*;
import com.example.demo.service.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coordinador")
@PreAuthorize("hasRole('COORDINADOR')")
public class CoordinadorController {

    private final UsuarioService usuarioService;
    private final DocenteService docenteService;
    private final PeriodoService periodoService;
    private final AulaService aulaService;
    private final CursoService cursoService;
    private final CicloService cicloService;
    private final MatriculaService matriculaService;
    private final PagoService pagoService;

    public CoordinadorController(UsuarioService usuarioService,
                                 DocenteService docenteService,
                                 PeriodoService periodoService,
                                 AulaService aulaService,
                                 CursoService cursoService,
                                 CicloService cicloService,
                                 MatriculaService matriculaService,
                                 PagoService pagoService) {
        this.usuarioService = usuarioService;
        this.docenteService = docenteService;
        this.periodoService = periodoService;
        this.aulaService = aulaService;
        this.cursoService = cursoService;
        this.cicloService = cicloService;
        this.matriculaService = matriculaService;
        this.pagoService = pagoService;
    }

    // ==========================================
    // ── DASHBOARD ──
    // ==========================================
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalEstudiantes", usuarioService.listarEstudiantes().size());
        model.addAttribute("totalDocentes", docenteService.listar().size());
        model.addAttribute("totalMatriculas", matriculaService.listarTodas().size());
        model.addAttribute("totalCiclos", cicloService.listarTodos().size());
        model.addAttribute("totalPagos", pagoService.listarTodos().size());
        return "coordinador/dashboard";
    }

    // ==========================================
    // ── ESTUDIANTES ──
    // ==========================================
    @GetMapping("/estudiantes")
    public String listarEstudiantes(Model model) {
        model.addAttribute("estudiantes", usuarioService.listarEstudiantes());
        return "coordinador/estudiantes/lista";
    }

    @GetMapping("/estudiantes/nuevo")
    public String formNuevoEstudiante(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "coordinador/estudiantes/form";
    }

    @PostMapping("/estudiantes/nuevo")
    public String crearEstudiante(@ModelAttribute Usuario usuario) {
        usuarioService.crearEstudiante(usuario);
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/editar/{id}")
    public String formEditarEstudiante(@PathVariable Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id); 
        model.addAttribute("usuario", usuario);
        return "coordinador/estudiantes/form";
    }

    @PostMapping("/estudiantes/editar/{id}")
    public String actualizarEstudiante(@PathVariable Long id, @ModelAttribute Usuario usuario) {
        usuarioService.actualizarEstudiante(id, usuario);
        return "redirect:/coordinador/estudiantes";
    }

    @GetMapping("/estudiantes/eliminar/{id}")
    public String eliminarEstudiante(@PathVariable Long id, RedirectAttributes ra) {
        try {
            usuarioService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Estudiante eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/estudiantes";
    }

    // ==========================================
    // ── DOCENTES ──
    // ==========================================
    @GetMapping("/docentes")
    public String listarDocentes(Model model) {
        model.addAttribute("docentes", docenteService.listar());
        return "coordinador/docentes/lista";
    }

    @GetMapping("/docentes/nuevo")
    public String formNuevoDocente(Model model) {
        model.addAttribute("docente", new Docente());
        return "coordinador/docentes/form";
    }

    @PostMapping("/docentes/nuevo")
    public String registrarDocente(@ModelAttribute Docente docente, RedirectAttributes ra) {
        try {
            docenteService.registrar(docente);
            ra.addFlashAttribute("mensaje", "Docente registrado con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/docentes";
    }

    @GetMapping("/docentes/editar/{id}")
    public String formEditarDocente(@PathVariable Long id, Model model) {
        Docente docente = docenteService.obtenerPorId(id); 
        model.addAttribute("docente", docente);
        return "coordinador/docentes/form";
    }

    @PostMapping("/docentes/editar/{id}")
    public String actualizarDocente(@PathVariable Long id, @ModelAttribute Docente docente, RedirectAttributes ra) {
        try {
            docenteService.actualizar(id, docente);
            ra.addFlashAttribute("mensaje", "Docente actualizado con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/docentes";
    }

    @GetMapping("/docentes/eliminar/{id}")
    public String eliminarDocente(@PathVariable Long id, RedirectAttributes ra) {
        try {
            docenteService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Docente eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/docentes";
    }

    // ==========================================
    // ── PERIODOS ──
    // ==========================================
    @GetMapping("/periodos")
    public String listarPeriodos(Model model) {
        model.addAttribute("periodos", periodoService.listar());
        return "coordinador/periodos/lista";
    }

    @GetMapping("/periodos/nuevo")
    public String formNuevoPeriodo(Model model) {
        model.addAttribute("periodo", new Periodo());
        model.addAttribute("estados", Periodo.Estado.values());
        return "coordinador/periodos/form";
    }

    @PostMapping("/periodos/nuevo")
    public String crearPeriodo(@ModelAttribute Periodo periodo) {
        periodoService.crear(periodo);
        return "redirect:/coordinador/periodos";
    }

    @GetMapping("/periodos/editar/{id}")
    public String formEditarPeriodo(@PathVariable Long id, Model model) {
        Periodo periodo = periodoService.obtenerPorId(id);
        model.addAttribute("periodo", periodo);
        model.addAttribute("estados", Periodo.Estado.values());
        return "coordinador/periodos/form";
    }

    @PostMapping("/periodos/editar/{id}")
    public String actualizarPeriodo(@PathVariable Long id, @ModelAttribute Periodo periodo) {
        periodoService.actualizar(id, periodo);
        return "redirect:/coordinador/periodos";
    }

    @GetMapping("/periodos/eliminar/{id}")
    public String eliminarPeriodo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            periodoService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Periodo eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/periodos";
    }

    // ==========================================
    // ── CICLOS ──
    // ==========================================
    @GetMapping("/ciclos")
    public String listarCiclos(Model model) {
        model.addAttribute("ciclos", cicloService.listarTodos());
        return "coordinador/ciclos/lista";
    }

    @GetMapping("/ciclos/nuevo")
    public String formNuevoCiclo(Model model) {
        model.addAttribute("ciclo", new Ciclo());
        model.addAttribute("tiposCiclo", Ciclo.TipoCiclo.values());
        return "coordinador/ciclos/form";
    }

    @PostMapping("/ciclos/nuevo")
    public String crearCiclo(@ModelAttribute Ciclo ciclo) {
        cicloService.crear(ciclo);
        return "redirect:/coordinador/ciclos";
    }

    @GetMapping("/ciclos/editar/{id}")
    public String formEditarCiclo(@PathVariable Long id, Model model) {
        Ciclo ciclo = cicloService.obtenerPorId(id);
        model.addAttribute("ciclo", ciclo);
        model.addAttribute("tiposCiclo", Ciclo.TipoCiclo.values());
        return "coordinador/ciclos/form";
    }

    @PostMapping("/ciclos/editar/{id}")
    public String actualizarCiclo(@PathVariable Long id, @ModelAttribute Ciclo ciclo) {
        cicloService.actualizar(id, ciclo);
        return "redirect:/coordinador/ciclos";
    }

    @GetMapping("/ciclos/eliminar/{id}")
    public String eliminarCiclo(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cicloService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Ciclo eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/ciclos";
    }

    // ==========================================
    // ── AULAS ──
    // ==========================================
    @GetMapping("/aulas")
    public String listarAulas(Model model) {
        model.addAttribute("aulas", aulaService.listar());
        return "coordinador/aulas/lista";
    }

    @GetMapping("/aulas/nuevo")
    public String formNuevaAula(Model model) {
        model.addAttribute("aula", new Aula());
        return "coordinador/aulas/form";
    }

    @PostMapping("/aulas/nuevo")
    public String crearAula(@ModelAttribute Aula aula) {
        aulaService.crear(aula);
        return "redirect:/coordinador/aulas";
    }

    @GetMapping("/aulas/editar/{id}")
    public String formEditarAula(@PathVariable Long id, Model model) {
        Aula aula = aulaService.obtenerPorId(id);
        model.addAttribute("aula", aula);
        return "coordinador/aulas/form";
    }

    @PostMapping("/aulas/editar/{id}")
    public String actualizarAula(@PathVariable Long id, @ModelAttribute Aula aula) {
        aulaService.actualizar(id, aula);
        return "redirect:/coordinador/aulas";
    }

    @GetMapping("/aulas/eliminar/{id}")
    public String eliminarAula(@PathVariable Long id, RedirectAttributes ra) {
        try {
            aulaService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Aula eliminada correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/aulas";
    }

    // ==========================================
    // ── CURSOS ──
    // ==========================================
    @GetMapping("/cursos")
    public String listarCursos(Model model) {
        model.addAttribute("cursos", cursoService.listar());
        return "coordinador/cursos/lista";
    }

    @GetMapping("/cursos/nuevo")
    public String formNuevoCurso(Model model) {
        model.addAttribute("curso", new Curso());
        model.addAttribute("docentes", docenteService.listar());
        model.addAttribute("ciclos", cicloService.listarTodos());
        model.addAttribute("aulas", aulaService.listar());
        model.addAttribute("periodos", periodoService.listar());
        return "coordinador/cursos/form";
    }

    @PostMapping("/cursos/nuevo")
    public String crearCurso(@ModelAttribute Curso curso) {
        cursoService.crear(curso);
        return "redirect:/coordinador/cursos";
    }

    @GetMapping("/cursos/editar/{id}")
    public String formEditarCurso(@PathVariable Long id, Model model) {
        Curso curso = cursoService.obtenerPorId(id);
        model.addAttribute("curso", curso);
        model.addAttribute("docentes", docenteService.listar());
        model.addAttribute("ciclos", cicloService.listarTodos());
        model.addAttribute("aulas", aulaService.listar());
        model.addAttribute("periodos", periodoService.listar());
        return "coordinador/cursos/form";
    }

    @PostMapping("/cursos/editar/{id}")
    public String actualizarCurso(@PathVariable Long id, @ModelAttribute Curso curso) {
        cursoService.actualizar(id, curso);
        return "redirect:/coordinador/cursos";
    }

    @GetMapping("/cursos/eliminar/{id}")
    public String eliminarCurso(@PathVariable Long id, RedirectAttributes ra) {
        try {
            cursoService.eliminar(id);
            ra.addFlashAttribute("mensaje", "Curso eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/cursos";
    }

    // ==========================================
    // ── MATRÍCULAS ──
    // ==========================================
    @GetMapping("/matriculas")
    public String listarMatriculas(Model model) {
        model.addAttribute("matriculas", matriculaService.listarTodas());
        return "coordinador/matriculas/lista";
    }

    @GetMapping("/matriculas/nueva")
    public String formNuevaMatricula(Model model) {
        model.addAttribute("estudiantes", usuarioService.listarEstudiantes());
        model.addAttribute("ciclos", cicloService.listarTodos());
        model.addAttribute("periodos", periodoService.listar());
        return "coordinador/matriculas/form";
    }

    @PostMapping("/matriculas/nueva")
    public String realizarMatricula(@RequestParam Long estudianteId,
                                    @RequestParam Long cicloId,
                                    @RequestParam Long periodoId) {
        matriculaService.matricular(estudianteId, cicloId, periodoId);
        return "redirect:/coordinador/matriculas";
    }

    @GetMapping("/matriculas/editar/{id}")
    public String formEditarMatricula(@PathVariable Long id, Model model) {
        Matricula matricula = matriculaService.obtenerPorId(id);
        model.addAttribute("matricula", matricula);
        model.addAttribute("estudiantes", usuarioService.listarEstudiantes());
        model.addAttribute("ciclos", cicloService.listarTodos());
        model.addAttribute("periodos", periodoService.listar());
        return "coordinador/matriculas/form";
    }

    /* ── CORREGIDO: Ahora recibe los parámetros sueltos del HTML y llama correctamente al servicio ── */
    @PostMapping("/matriculas/editar/{id}")
    public String actualizarMatricula(@PathVariable Long id, 
                                      @RequestParam Long estudianteId,
                                      @RequestParam Long cicloId,
                                      @RequestParam Long periodoId, 
                                      RedirectAttributes ra) {
        try {
            matriculaService.actualizar(id, estudianteId, cicloId, periodoId);
            ra.addFlashAttribute("mensaje", "Matrícula actualizada con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/matriculas";
    }
    
    // ==========================================
    // ── PAGOS ──
    // ==========================================
    @GetMapping("/pagos")
    public String listarPagos(Model model) {
        model.addAttribute("pagos", pagoService.listarTodos());
        return "coordinador/pagos/lista";
    }

    @GetMapping("/pagos/nuevo/{matriculaId}")
    public String formNuevoPago(@PathVariable Long matriculaId, Model model) {
        model.addAttribute("pago", new Pago());
        model.addAttribute("matriculaId", matriculaId);
        model.addAttribute("metodos", Pago.MetodoPago.values());
        return "coordinador/pagos/form";
    }

    @PostMapping("/pagos/nuevo/{matriculaId}")
    public String crearPago(@PathVariable Long matriculaId, @ModelAttribute Pago pago) {
        pagoService.registrarPago(matriculaId, pago);
        return "redirect:/coordinador/pagos";
    }

    @GetMapping("/pagos/editar/{id}")
    public String formEditarPago(@PathVariable Long id, Model model) {
        Pago pago = pagoService.obtenerPorId(id);
        model.addAttribute("pago", pago);
        model.addAttribute("metodos", Pago.MetodoPago.values());
        return "coordinador/pagos/form";
    }

    /* ── CORREGIDO: Sincronizado con los nuevos métodos del PagoService ── */
    @PostMapping("/pagos/editar/{id}")
    public String actualizarPago(@PathVariable Long id, @ModelAttribute Pago pago, RedirectAttributes ra) {
        try {
            pagoService.actualizar(id, pago);
            ra.addFlashAttribute("mensaje", "Pago actualizado con éxito.");
        } catch (Exception e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/coordinador/pagos";
    }
}