package com.fiap.m4g.clearvolt.domain.repository;

import com.fiap.m4g.clearvolt.domain.entity.Dispositivo;
import com.fiap.m4g.clearvolt.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface DispositivoRepository extends JpaRepository<Dispositivo, Long> {
    List<Dispositivo> findAllByDono(Optional<Usuario> dono);
    Optional<Dispositivo> findByIdentificador(String identificador);
}
