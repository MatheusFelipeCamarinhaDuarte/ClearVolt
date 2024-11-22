package com.fiap.m4g.clearvolt.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "ROLES", uniqueConstraints = {
        /**
         * UK para garantir que não se tenha nome de cargos iguais.
         */
        @UniqueConstraint(name = "UK_NOME_DO_ROLE", columnNames = {"NM_ROLE"})
})
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ROLE")
    private long id;

    @Column(name = "NM_ROLE")
    private String name;
}
