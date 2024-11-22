package com.fiap.m4g.clearvolt.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DISPOSITIVOS", uniqueConstraints = {
        /**
         * UK para garantir que não se tenha mais de um dispositivo com o mesmo identificador.
         */
        @UniqueConstraint(name = "UK_IDENTIFICADOR", columnNames = {"IDENTIFICADOR"})
})
public class Dispositivo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DISPOSITIVO")
    private long id;

    @NotNull(message = "Este campo não pode ser vazio")
    @Column(name = "NM_DISPOSITIVO")
    private String nome;

    @NotNull(message = "Este campo não pode ser vazio")
    @Column(name = "MARCA")
    private String marca;

    @Pattern(regexp = "^[0-9]{3}[A-Za-z]{2}[0-9]{1}[A-Za-z]{2}[0-9]{1}$", message = "O identificador deve ter o formato NNNLLNLLN")
    @Column(name = "IDENTIFICADOR")
    private String identificador;

    @ManyToOne(fetch = FetchType.EAGER, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinColumn(
            name = "DONO",
            referencedColumnName = "ID_USER",
            foreignKey = @ForeignKey(name = "FK_DONO_DO_DISPOSITIVO"),
            nullable = false
    )
    private Usuario dono;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
            name = "CONFIG_ATUAL",
            referencedColumnName = "ID_CONFIG",
            foreignKey = @ForeignKey(name = "FK_CONFIGURACAO_ATUAL_DO_DISPOSITVO")
    )
    private ConfiguracaoDeColeta configAtual;
}
