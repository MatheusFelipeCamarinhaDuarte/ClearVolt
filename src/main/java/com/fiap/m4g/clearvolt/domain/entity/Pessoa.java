package com.fiap.m4g.clearvolt.domain.entity;

import com.fiap.m4g.clearvolt.infra.exception.MaiorDeIdade;
import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PESSOAS", uniqueConstraints = {
        /**
         * UK para garantir que não se tenha CPFs iguais.
         */
        @UniqueConstraint(name = "UK_PESSOA_CPF", columnNames = {"CPF"})
})
public class Pessoa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PESSOA")
    private long id;

    @Size(min = 3, max = 30, message = "O nome deve estar entre 3 e 30 caracteres")
    @Column(name = "NM_PESSOA")
    private String nome;

    @Size(min = 3, max = 30, message = "O sobrenome deve estar entre 3 e 30 caracteres")
    @Column(name = "SOBRENOME")
    private String sobrenome;

    @Past(message = "A data de nascimento deve estar no passado.")
    @MaiorDeIdade
    @Column(name = "DT_NASC")
    private LocalDate dataDeNascimento;

    @CPF(message = "Deve-se fornecer um CPF válido")
    @Column(name = "CPF")
    private String cpf;

    @Size(min = 11, message = "O telefone deve conter 11 Digitos")
    @Column(name = "TELEFONE")
    private String telefone;

}
