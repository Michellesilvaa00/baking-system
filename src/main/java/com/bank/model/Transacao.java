package com.bank.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transacoes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Transacao {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conta_id", nullable = false)
    private ContaCorrente conta;
    @Column(name = "numero_conta_destino", length = 10)
    private String numeroContaDestino;
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoTransacao tipo;
    @Column(name = "valor", nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;
    @Column(name = "saldo_antes", precision = 15, scale = 2)
    private BigDecimal saldoAntes;
    @Column(name = "saldo_depois", precision = 15, scale = 2)
    private BigDecimal saldoDepois;
    @Column(name = "descricao", length = 255)
    private String descricao;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusTransacao status;
    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora;
    @Column(name = "codigo_autorizacao", length = 20)
    private String codigoAutorizacao;

    @PrePersist
    protected void onCreate() {
        dataHora = LocalDateTime.now();
        if (status == null) status = StatusTransacao.CONCLUIDA;
        codigoAutorizacao = "AUTH" + System.currentTimeMillis();
    }
    public enum TipoTransacao { DEPOSITO, SAQUE, TRANSFERENCIA_ENVIADA, TRANSFERENCIA_RECEBIDA }
    public enum StatusTransacao { CONCLUIDA, PENDENTE, CANCELADA, FALHA }
}
