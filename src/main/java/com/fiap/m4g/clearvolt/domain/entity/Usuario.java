package com.fiap.m4g.clearvolt.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "USUARIOS", uniqueConstraints = {
        /**
         * UK para garantir que não se tenha EMAILS iguais.
         * UK para garantir que não haja uma mesma pessoa ligada ao mesmo usuário
         */
        @UniqueConstraint(name = "UK_USUARIO_EMAIL", columnNames = {"EMAIL"}),
        @UniqueConstraint(name = "UK_USUARIO_PESSOA", columnNames = {"PESSOA"})
})
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USER")
    private long id;

    @Email(message = "Email inválido")
    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PASSWORD")
    private String password;

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(
            name = "PESSOA",
            referencedColumnName = "ID_PESSOA",
            foreignKey = @ForeignKey(name = "FK_PESSOA_USUARIO"),
            nullable = false
    )
    @Valid
    private Pessoa pessoa;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "USER_ROLE",
            referencedColumnName = "ID_ROLE",
            foreignKey = @ForeignKey(name = "FK_USER_ROLE"),
            nullable = false
    )
    private Role userRole;
}
