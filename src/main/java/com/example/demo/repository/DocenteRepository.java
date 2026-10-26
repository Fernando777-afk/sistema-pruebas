package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.model.Docente;

@Repository
public interface DocenteRepository extends JpaRepository<Docente, Long> {

    boolean existsByDni(String dni);

}