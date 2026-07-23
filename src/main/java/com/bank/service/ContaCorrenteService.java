package com.bank.service;
import com.bank.dto.ContaDTO;
import com.bank.exception.BankingException;
import com.bank.model.ContaCorrente;
import com.bank.repository.ContaCorrenteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class ContaCorrenteService {
    private final ContaCorrenteRepository repo;

    @Transactional
    public ContaDTO.ContaResponse criarConta(ContaDTO.CriarContaRequest req) {
        if (repo.existsByCpf(formatarCpf(req.getCpf()))) throw new BankingException.CpfJaCadastradoException(req.getCpf());
        ContaCorrente c = ContaCorrente.builder()
            .numeroConta(gerarNumeroConta()).agencia(req.getAgencia()).titular(req.getTitular())
            .cpf(formatarCpf(req.getCpf()))
            .saldo(req.getDepositoInicial() != null ? req.getDepositoInicial() : BigDecimal.ZERO)
            .limiteChequeEspecial(req.getLimiteChequeEspecial() != null ? req.getLimiteChequeEspecial() : BigDecimal.ZERO)
            .status(ContaCorrente.StatusConta.ATIVA).build();
        return toResponse(repo.save(c));
    }
    @Transactional(readOnly = true)
    public ContaDTO.ContaResponse buscarPorNumeroConta(String n) {
        return toResponse(repo.findByNumeroConta(n).orElseThrow(() -> new BankingException.ContaNaoEncontradaException(n)));
    }
    @Transactional(readOnly = true)
    public ContaDTO.ContaResponse buscarPorCpf(String cpf) {
        return toResponse(repo.findByCpf(formatarCpf(cpf)).orElseThrow(() -> new BankingException.ContaNaoEncontradaException("CPF: " + cpf)));
    }
    @Transactional(readOnly = true)
    public List<ContaDTO.ContaResponse> listarTodas() { return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList()); }
    @Transactional(readOnly = true)
    public List<ContaDTO.ContaResponse> listarPorStatus(ContaCorrente.StatusConta s) { return repo.findByStatus(s).stream().map(this::toResponse).collect(Collectors.toList()); }
    @Transactional
    public ContaDTO.ContaResponse atualizarStatus(String n, ContaCorrente.StatusConta s) {
        ContaCorrente c = repo.findByNumeroConta(n).orElseThrow(() -> new BankingException.ContaNaoEncontradaException(n));
        c.setStatus(s); return toResponse(repo.save(c));
    }
    public ContaCorrente buscarEntidadePorNumeroConta(String n) {
        return repo.findByNumeroConta(n).orElseThrow(() -> new BankingException.ContaNaoEncontradaException(n));
    }
    public void salvar(ContaCorrente c) { repo.save(c); }
    private String gerarNumeroConta() {
        String n; Random rnd = new Random();
        do { n = String.format("%010d", (long)(rnd.nextDouble() * 9_000_000_000L) + 1_000_000_000L); } while (repo.existsByNumeroConta(n));
        return n;
    }
    private String formatarCpf(String cpf) {
        String d = cpf.replaceAll("[^0-9]", "");
        if (d.length() == 11) return d.substring(0,3)+"."+d.substring(3,6)+"."+d.substring(6,9)+"-"+d.substring(9);
        return cpf;
    }
    public ContaDTO.ContaResponse toResponse(ContaCorrente c) {
        return ContaDTO.ContaResponse.builder().id(c.getId()).numeroConta(c.getNumeroConta()).agencia(c.getAgencia())
            .titular(c.getTitular()).cpf(c.getCpf()).saldo(c.getSaldo()).limiteChequeEspecial(c.getLimiteChequeEspecial())
            .saldoDisponivel(c.getSaldoDisponivel()).status(c.getStatus()).dataAbertura(c.getDataAbertura()).dataAtualizacao(c.getDataAtualizacao()).build();
    }
}
