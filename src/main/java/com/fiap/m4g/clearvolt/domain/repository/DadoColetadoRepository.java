package com.fiap.m4g.clearvolt.domain.repository;

import com.fiap.m4g.clearvolt.domain.entity.DadoColetado;
import com.fiap.m4g.clearvolt.domain.entity.Dispositivo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DadoColetadoRepository extends JpaRepository<DadoColetado, Long> {
    void deleteAllByDispositivoPertencente(Dispositivo dispositivo);
}
