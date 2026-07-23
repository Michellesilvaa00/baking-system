package com.bank.dto;
import com.bank.model.ContaCorrente;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.validator.constraints.br.CPF;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContaDTO {
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class CriarContaRequest {
        @NotBlank(message = "Nome do titular é obrigatório")
        @Size(min = 3, max = 100)
        private String titular;
        @NotBlank(message = "CPF é obrigatório")
        @CPF(message = "CPF inválido")
        private String cpf;
        @NotBlank(message = "Agência é obrigatória")
        private String agencia;
        @DecimalMin(value = "0.00", message = "Depósito inicial não pode ser negativo")
        private BigDecimal depositoInicial;
        @DecimalMin(value = "0.00")
        private BigDecimal limiteChequeEspecial;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ContaResponse {
        private Long id;
        private String numeroConta;
        private String agencia;
        private String titular;
        private String cpf;
        private BigDecimal saldo;
        private BigDecimal limiteChequeEspecial;
        private BigDecimal saldoDisponivel;
        private ContaCorrente.StatusConta status;
        private LocalDateTime dataAbertura;
        private LocalDateTime dataAtualizacao;
    }
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class AtualizarStatusRequest {
        @NotNull(message = "Status é obrigatório")
        private ContaCorrente.StatusConta status;
    }
}
