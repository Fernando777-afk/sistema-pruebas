// UsuarioRepository.java
package com.example.demo.repository;

import com.example.demo.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    boolean existsByDni(String dni);
    boolean existsByUsername(String username);
    List<Usuario> findByRol(Usuario.Rol rol);
}