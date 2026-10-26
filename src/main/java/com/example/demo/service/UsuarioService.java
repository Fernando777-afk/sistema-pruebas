package com.example.demo.service;

import com.example.demo.model.Usuario;
import com.example.demo.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Usuario crearEstudiante(Usuario usuario) {
        if (usuarioRepository.existsByDni(usuario.getDni()))
            throw new RuntimeException("Ya existe un usuario con ese DNI");
        if (usuarioRepository.existsByUsername(usuario.getUsername()))
            throw new RuntimeException("El username ya está en uso");
        usuario.setPassword(passwordEncoder.encode(usuario.getPassword()));
        usuario.setRol(Usuario.Rol.ESTUDIANTE);
        usuario.setEstado(Usuario.Estado.ACTIVO);
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarEstudiantes() {
        return usuarioRepository.findByRol(Usuario.Rol.ESTUDIANTE);
    }

    public Usuario obtenerPorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public Usuario actualizarEstudiante(Long id, Usuario datos) {
        Usuario u = obtenerPorId(id);
        u.setNombre(datos.getNombre());
        u.setApellido(datos.getApellido());
        u.setEmail(datos.getEmail());
        u.setTelefono(datos.getTelefono());
        u.setEstado(datos.getEstado());
        return usuarioRepository.save(u);
    }
    
    public void eliminar(Long id) {
        if (!usuarioRepository.existsById(id))
            throw new RuntimeException("Usuario no encontrado");
        usuarioRepository.deleteById(id);
    }
}