package com.bank.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "contas_correntes")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ContaCorrente {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "numero_conta", unique = true, nullable = false, length = 10)
    private String numeroConta;
    @Column(name = "agencia", nullable = false, length = 6)
    private String agencia;
    @Column(name = "titular", nullable = false, length = 100)
    private String titular;
    @Column(name = "cpf", unique = true, nullable = false, length = 14)
    private String cpf;
    @Column(name = "saldo", nullable = false, precision = 15, scale = 2)
    private BigDecimal saldo;
    @Column(name = "limite_cheque_especial", precision = 15, scale = 2)
    private BigDecimal limiteChequeEspecial;
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConta status;
    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoes;

    @PrePersist
    protected void onCreate() {
        dataAbertura = LocalDateTime.now();
        dataAtualizacao = LocalDateTime.now();
        if (saldo == null) saldo = BigDecimal.ZERO;
        if (limiteChequeEspecial == null) limiteChequeEspecial = BigDecimal.ZERO;
        if (status == null) status = StatusConta.ATIVA;
    }
    @PreUpdate
    protected void onUpdate() { dataAtualizacao = LocalDateTime.now(); }
    public BigDecimal getSaldoDisponivel() { return saldo.add(limiteChequeEspecial); }
    public boolean temSaldoSuficiente(BigDecimal valor) { return getSaldoDisponivel().compareTo(valor) >= 0; }
    public enum StatusConta { ATIVA, INATIVA, BLOQUEADA, ENCERRADA }
}
