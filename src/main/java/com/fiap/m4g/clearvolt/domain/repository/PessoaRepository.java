package com.fiap.m4g.clearvolt.domain.repository;

import com.fiap.m4g.clearvolt.domain.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
}
