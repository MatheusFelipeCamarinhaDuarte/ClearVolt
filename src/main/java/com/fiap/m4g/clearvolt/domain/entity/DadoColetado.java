package com.fiap.m4g.clearvolt.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "DADOS_COLETADOS", uniqueConstraints = {
        /**
         * UK para garantir que não se tenha um dado duplicado, comparando o IDENTIFICADOR com o Datatime
         */
        @UniqueConstraint(name = "UK_IDENTIFICADORA_DE_DADOS_DUPLICADOS", columnNames = {"DATATIME_DO_DADO_COLETADO", "IDENTIFICADOR"})
})
public class DadoColetado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DADO_COLETADO")
    private long id;

    @Column(name = "TEMPERATURA")
    private int temperatura;

    @Column(name = "UMIDADE")
    private int umidade;

    @Column(name = "DATATIME_DO_DADO_COLETADO")
    private LocalDateTime datatimeDoDadoColetado;

    @Column(name = "IDENTIFICADOR")
    private String identificador;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REMOVE)
    @JoinColumn(
            name = "DISPOSITIVO_PERTENCENTE",
            referencedColumnName = "ID_DISPOSITIVO",
            foreignKey = @ForeignKey(name = "FK_DISPOSITIVO_PERTENCENTE")
    )
    private Dispositivo dispositivoPertencente;

}
