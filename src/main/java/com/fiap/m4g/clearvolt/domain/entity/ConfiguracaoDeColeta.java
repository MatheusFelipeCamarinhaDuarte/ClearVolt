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
@Table(name = "CONFIGURACOES_DE_COLETA", uniqueConstraints = {
        /**
         * UK para garantir que não tenha uma configuração personalizada com o mesmo nome por usuário.
         */
        @UniqueConstraint(name = "UK_PARA_IDENTIFICAR_CONFIG_DUPLICADA", columnNames = {"NM_CONFIG", "USUARIO_PERTENCENTE"})
})
public class ConfiguracaoDeColeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CONFIG")
    private long id;

    @Column(name = "NM_CONFIG")
    private String nome;

    @Column(name = "DESCRICAO_CONFIG")
    private String descricao;

    @Column(name = "TEMPERATURA_MAX")
    private int tempMax;

    @Column(name = "UMIDADE_MIN")
    private int umidadeMin;

    @Column(name = "TEMPO_DE_UMIDADE_MIN")
    private int tempoDeUmidadeMin;

    @Column(name = "INTERVALO_DE_HORAS")
    private int intervaloDeHoras;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
    @JoinColumn(
            name = "USUARIO_PERTENCENTE",
            referencedColumnName = "ID_USER",
            foreignKey = @ForeignKey(name = "FK_USUARIO_PERTENCENTE")
    )
    private Usuario usuarioPertencente;
}
