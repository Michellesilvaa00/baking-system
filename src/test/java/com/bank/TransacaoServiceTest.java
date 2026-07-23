package com.bank;
import com.bank.dto.TransacaoDTO;
import com.bank.exception.BankingException;
import com.bank.model.ContaCorrente;
import com.bank.model.Transacao;
import com.bank.repository.TransacaoRepository;
import com.bank.service.ContaCorrenteService;
import com.bank.service.TransacaoService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {
    @Mock private TransacaoRepository transacaoRepo;
    @Mock private ContaCorrenteService contaService;
    @InjectMocks private TransacaoService transacaoService;

    private ContaCorrente contaAtiva, contaDestino;

    @BeforeEach void setup() {
        contaAtiva = ContaCorrente.builder().id(1L).numeroConta("0000000001").titular("João Silva")
            .cpf("123.456.789-00").agencia("0001").saldo(new BigDecimal("1000.00"))
            .limiteChequeEspecial(BigDecimal.ZERO).status(ContaCorrente.StatusConta.ATIVA).build();
        contaDestino = ContaCorrente.builder().id(2L).numeroConta("0000000002").titular("Maria Souza")
            .cpf("987.654.321-00").agencia("0001").saldo(new BigDecimal("500.00"))
            .limiteChequeEspecial(BigDecimal.ZERO).status(ContaCorrente.StatusConta.ATIVA).build();
    }

    @Test @DisplayName("Depósito deve aumentar saldo")
    void deposito() {
        when(contaService.buscarEntidadePorNumeroConta("0000000001")).thenReturn(contaAtiva);
        when(transacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        transacaoService.depositar("0000000001", new TransacaoDTO.DepositoRequest(new BigDecimal("200.00"), "Teste"));
        assertEquals(new BigDecimal("1200.00"), contaAtiva.getSaldo());
    }

    @Test @DisplayName("Saque deve diminuir saldo")
    void saque() {
        when(contaService.buscarEntidadePorNumeroConta("0000000001")).thenReturn(contaAtiva);
        when(transacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        transacaoService.sacar("0000000001", new TransacaoDTO.SaqueRequest(new BigDecimal("300.00"), "Teste"));
        assertEquals(new BigDecimal("700.00"), contaAtiva.getSaldo());
    }

    @Test @DisplayName("Saque com saldo insuficiente lança exceção")
    void saqueInsuficiente() {
        when(contaService.buscarEntidadePorNumeroConta("0000000001")).thenReturn(contaAtiva);
        assertThrows(BankingException.SaldoInsuficienteException.class,
            () -> transacaoService.sacar("0000000001", new TransacaoDTO.SaqueRequest(new BigDecimal("2000.00"), null)));
    }

    @Test @DisplayName("Transferência move saldo entre contas")
    void transferencia() {
        when(contaService.buscarEntidadePorNumeroConta("0000000001")).thenReturn(contaAtiva);
        when(contaService.buscarEntidadePorNumeroConta("0000000002")).thenReturn(contaDestino);
        when(transacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        transacaoService.transferir("0000000001", new TransacaoDTO.TransferenciaRequest("0000000002", new BigDecimal("400.00"), null));
        assertEquals(new BigDecimal("600.00"), contaAtiva.getSaldo());
        assertEquals(new BigDecimal("900.00"), contaDestino.getSaldo());
    }

    @Test @DisplayName("Transferência para mesma conta lança exceção")
    void transferenciaMesmaConta() {
        assertThrows(BankingException.TransferenciaMesmaContaException.class,
            () -> transacaoService.transferir("0000000001", new TransacaoDTO.TransferenciaRequest("0000000001", new BigDecimal("100.00"), null)));
    }

    @Test @DisplayName("Saque com cheque especial funciona")
    void saqueComChequeEspecial() {
        contaAtiva.setLimiteChequeEspecial(new BigDecimal("500.00"));
        when(contaService.buscarEntidadePorNumeroConta("0000000001")).thenReturn(contaAtiva);
        when(transacaoRepo.save(any())).thenAnswer(i -> i.getArgument(0));
        assertDoesNotThrow(() -> transacaoService.sacar("0000000001", new TransacaoDTO.SaqueRequest(new BigDecimal("1300.00"), null)));
        assertEquals(new BigDecimal("-300.00"), contaAtiva.getSaldo());
    }
}
