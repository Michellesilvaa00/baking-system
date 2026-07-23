package com.bank;

import com.bank.dto.ContaDTO;
import com.bank.dto.TransacaoDTO;
import com.bank.exception.BankingException;
import com.bank.model.ContaCorrente;
import com.bank.service.ContaCorrenteService;
import com.bank.service.TransacaoService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class BankingIntegrationTest {

    @Autowired ContaCorrenteService contaService;
    @Autowired TransacaoService transacaoService;

    static String numeroConta1;
    static String numeroConta2;

    @Test @Order(1)
    @DisplayName("✅ Criar Conta 1 - João Silva")
    void criarConta1() {
        var req = new ContaDTO.CriarContaRequest();
        req.setTitular("João Silva");
        req.setCpf("529.982.247-25");
        req.setAgencia("0001");
        req.setDepositoInicial(new BigDecimal("1000.00"));
        req.setLimiteChequeEspecial(new BigDecimal("500.00"));

        var resp = contaService.criarConta(req);
        numeroConta1 = resp.getNumeroConta();

        assertNotNull(resp.getId());
        assertNotNull(numeroConta1);
        assertEquals("João Silva", resp.getTitular());
        assertEquals(new BigDecimal("1000.00"), resp.getSaldo());
        assertEquals(new BigDecimal("500.00"), resp.getLimiteChequeEspecial());
        assertEquals(new BigDecimal("1500.00"), resp.getSaldoDisponivel());
        assertEquals(ContaCorrente.StatusConta.ATIVA, resp.getStatus());
        System.out.println("  → Conta criada: " + numeroConta1 + " | Saldo: R$ " + resp.getSaldo());
    }

    @Test @Order(2)
    @DisplayName("✅ Criar Conta 2 - Maria Souza")
    void criarConta2() {
        var req = new ContaDTO.CriarContaRequest();
        req.setTitular("Maria Souza");
        req.setCpf("275.320.458-56");
        req.setAgencia("0001");
        req.setDepositoInicial(new BigDecimal("500.00"));

        var resp = contaService.criarConta(req);
        numeroConta2 = resp.getNumeroConta();

        assertNotNull(numeroConta2);
        assertEquals(new BigDecimal("500.00"), resp.getSaldo());
        System.out.println("  → Conta criada: " + numeroConta2 + " | Saldo: R$ " + resp.getSaldo());
    }

    @Test @Order(3)
    @DisplayName("❌ CPF duplicado deve lançar exceção")
    void cpfDuplicado() {
        var req = new ContaDTO.CriarContaRequest();
        req.setTitular("João Clone");
        req.setCpf("529.982.247-25");
        req.setAgencia("0001");
        assertThrows(BankingException.CpfJaCadastradoException.class, () -> contaService.criarConta(req));
        System.out.println("  → CPF duplicado rejeitado corretamente");
    }

    @Test @Order(4)
    @DisplayName("✅ Depósito na Conta 1")
    void depositar() {
        var req = new TransacaoDTO.DepositoRequest(new BigDecimal("250.00"), "Salário");
        var resp = transacaoService.depositar(numeroConta1, req);

        assertEquals(new BigDecimal("1250.00"), resp.getSaldoDepois());
        assertNotNull(resp.getCodigoAutorizacao());
        System.out.println("  → Depósito R$ 250,00 | Novo saldo: R$ " + resp.getSaldoDepois());
    }

    @Test @Order(5)
    @DisplayName("✅ Saque da Conta 1")
    void sacar() {
        var req = new TransacaoDTO.SaqueRequest(new BigDecimal("300.00"), "Pagamento conta");
        var resp = transacaoService.sacar(numeroConta1, req);

        assertEquals(new BigDecimal("950.00"), resp.getSaldoDepois());
        System.out.println("  → Saque R$ 300,00 | Novo saldo: R$ " + resp.getSaldoDepois());
    }

    @Test @Order(6)
    @DisplayName("✅ Saque usando cheque especial")
    void saqueComChequeEspecial() {
        // Saldo atual: 950, limite: 500, disponível: 1450
        var req = new TransacaoDTO.SaqueRequest(new BigDecimal("1200.00"), "Emergência");
        var resp = transacaoService.sacar(numeroConta1, req);

        assertEquals(new BigDecimal("-250.00"), resp.getSaldoDepois());
        System.out.println("  → Saque R$ 1200,00 (usando cheque especial) | Saldo: R$ " + resp.getSaldoDepois());
    }

    @Test @Order(7)
    @DisplayName("❌ Saque com saldo insuficiente")
    void saqueInsuficiente() {
        // Saldo: -250, limite: 500, disponível: 250
        var req = new TransacaoDTO.SaqueRequest(new BigDecimal("1000.00"), "Saque impossível");
        assertThrows(BankingException.SaldoInsuficienteException.class,
            () -> transacaoService.sacar(numeroConta1, req));
        System.out.println("  → Saque insuficiente rejeitado corretamente");
    }

    @Test @Order(8)
    @DisplayName("✅ Depositar para recompor saldo antes da transferência")
    void recomporSaldo() {
        var req = new TransacaoDTO.DepositoRequest(new BigDecimal("500.00"), "Depósito recomposição");
        var resp = transacaoService.depositar(numeroConta1, req);
        assertEquals(new BigDecimal("250.00"), resp.getSaldoDepois());
        System.out.println("  → Saldo recomposto: R$ " + resp.getSaldoDepois());
    }

    @Test @Order(9)
    @DisplayName("✅ Transferência entre contas")
    void transferencia() {
        // Conta1: 250, Conta2: 500 → transfere 200
        var req = new TransacaoDTO.TransferenciaRequest(numeroConta2, new BigDecimal("200.00"), "Pagamento aluguel");
        var resp = transacaoService.transferir(numeroConta1, req);

        assertEquals(new BigDecimal("50.00"), resp.getSaldoDepois());

        var conta2 = contaService.buscarPorNumeroConta(numeroConta2);
        assertEquals(new BigDecimal("700.00"), conta2.getSaldo());
        System.out.println("  → Transferência R$ 200,00 | C1: R$ " + resp.getSaldoDepois() + " | C2: R$ " + conta2.getSaldo());
    }

    @Test @Order(10)
    @DisplayName("❌ Transferência para mesma conta")
    void transferenciaMesmaConta() {
        var req = new TransacaoDTO.TransferenciaRequest(numeroConta1, new BigDecimal("10.00"), null);
        assertThrows(BankingException.TransferenciaMesmaContaException.class,
            () -> transacaoService.transferir(numeroConta1, req));
        System.out.println("  → Transferência para si mesmo rejeitada");
    }

    @Test @Order(11)
    @DisplayName("✅ Extrato com todas as transações")
    void extrato() {
        var extrato = transacaoService.consultarExtrato(numeroConta1);
        assertNotNull(extrato);
        assertFalse(extrato.getTransacoes().isEmpty());
        System.out.println("  → Extrato com " + extrato.getTransacoes().size() + " transações | Saldo: R$ " + extrato.getSaldoAtual());
        extrato.getTransacoes().forEach(t ->
            System.out.println("      [" + t.getTipo() + "] R$ " + t.getValor() + " → saldo: R$ " + t.getSaldoDepois())
        );
    }

    @Test @Order(12)
    @DisplayName("✅ Buscar conta por CPF")
    void buscarPorCpf() {
        var conta = contaService.buscarPorCpf("529.982.247-25");
        assertEquals("João Silva", conta.getTitular());
        System.out.println("  → Busca por CPF OK: " + conta.getTitular());
    }

    @Test @Order(13)
    @DisplayName("✅ Atualizar status da conta")
    void atualizarStatus() {
        contaService.atualizarStatus(numeroConta2, ContaCorrente.StatusConta.BLOQUEADA);
        var conta = contaService.buscarPorNumeroConta(numeroConta2);
        assertEquals(ContaCorrente.StatusConta.BLOQUEADA, conta.getStatus());
        System.out.println("  → Conta " + numeroConta2 + " bloqueada com sucesso");
    }

    @Test @Order(14)
    @DisplayName("❌ Operação em conta bloqueada")
    void operacaoContaBloqueada() {
        var req = new TransacaoDTO.DepositoRequest(new BigDecimal("100.00"), "Teste");
        assertThrows(BankingException.ContaInativaException.class,
            () -> transacaoService.depositar(numeroConta2, req));
        System.out.println("  → Operação em conta bloqueada rejeitada");
    }

    @Test @Order(15)
    @DisplayName("❌ Conta não encontrada")
    void contaNaoEncontrada() {
        assertThrows(BankingException.ContaNaoEncontradaException.class,
            () -> contaService.buscarPorNumeroConta("9999999999"));
        System.out.println("  → Conta inexistente tratada corretamente");
    }

    @Test @Order(16)
    @DisplayName("✅ Listar todas as contas")
    void listarContas() {
        var contas = contaService.listarTodas();
        assertEquals(2, contas.size());
        System.out.println("  → Total de contas: " + contas.size());
    }
}
