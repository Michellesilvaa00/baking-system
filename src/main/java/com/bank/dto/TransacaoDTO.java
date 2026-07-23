package com.bank.dto;
import com.bank.model.Transacao;
import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class TransacaoDTO {
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class SaqueRequest {
        @NotNull @DecimalMin(value = "0.01", message = "Valor mínimo de saque é R$ 0,01")
        private BigDecimal valor;
        private String descricao;
    }
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class DepositoRequest {
        @NotNull @DecimalMin(value = "0.01", message = "Valor mínimo de depósito é R$ 0,01")
        private BigDecimal valor;
        private String descricao;
    }
    @Data @NoArgsConstructor @AllArgsConstructor
    public static class TransferenciaRequest {
        @NotBlank(message = "Número da conta destino é obrigatório")
        private String numeroContaDestino;
        @NotNull @DecimalMin(value = "0.01")
        private BigDecimal valor;
        private String descricao;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class TransacaoResponse {
        private Long id;
        private String numeroConta;
        private String numeroContaDestino;
        private Transacao.TipoTransacao tipo;
        private BigDecimal valor;
        private BigDecimal saldoAntes;
        private BigDecimal saldoDepois;
        private String descricao;
        private Transacao.StatusTransacao status;
        private LocalDateTime dataHora;
        private String codigoAutorizacao;
        private String mensagem;
    }
    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class ExtratoResponse {
        private String numeroConta;
        private String titular;
        private BigDecimal saldoAtual;
        private BigDecimal saldoDisponivel;
        private List<TransacaoResponse> transacoes;
        private LocalDateTime dataConsulta;
    }
}
