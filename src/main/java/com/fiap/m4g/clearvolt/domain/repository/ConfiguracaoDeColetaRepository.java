package com.fiap.m4g.clearvolt.domain.repository;

import com.fiap.m4g.clearvolt.domain.entity.ConfiguracaoDeColeta;
import com.fiap.m4g.clearvolt.domain.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConfiguracaoDeColetaRepository extends JpaRepository<ConfiguracaoDeColeta, Long> {
    Optional<ConfiguracaoDeColeta> findByNome(String nome);
    List<ConfiguracaoDeColeta> findAllByUsuarioPertencente(Usuario usuario);
}
