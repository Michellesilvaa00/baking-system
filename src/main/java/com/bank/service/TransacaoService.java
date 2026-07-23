package com.bank.service;
import com.bank.dto.TransacaoDTO;
import com.bank.exception.BankingException;
import com.bank.model.ContaCorrente;
import com.bank.model.Transacao;
import com.bank.repository.TransacaoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j @Service @RequiredArgsConstructor
public class TransacaoService {
    private final TransacaoRepository transacaoRepo;
    private final ContaCorrenteService contaService;

    @Transactional
    public TransacaoDTO.TransacaoResponse depositar(String numeroConta, TransacaoDTO.DepositoRequest req) {
        validarValor(req.getValor(), "depósito");
        ContaCorrente c = contaService.buscarEntidadePorNumeroConta(numeroConta);
        validarAtiva(c);
        BigDecimal antes = c.getSaldo();
        c.setSaldo(antes.add(req.getValor()));
        contaService.salvar(c);
        Transacao t = Transacao.builder().conta(c).tipo(Transacao.TipoTransacao.DEPOSITO).valor(req.getValor())
            .saldoAntes(antes).saldoDepois(c.getSaldo())
            .descricao(req.getDescricao() != null ? req.getDescricao() : "Depósito")
            .status(Transacao.StatusTransacao.CONCLUIDA).build();
        return toResponse(transacaoRepo.save(t), "Depósito realizado com sucesso");
    }

    @Transactional
    public TransacaoDTO.TransacaoResponse sacar(String numeroConta, TransacaoDTO.SaqueRequest req) {
        validarValor(req.getValor(), "saque");
        ContaCorrente c = contaService.buscarEntidadePorNumeroConta(numeroConta);
        validarAtiva(c);
        if (!c.temSaldoSuficiente(req.getValor())) throw new BankingException.SaldoInsuficienteException();
        BigDecimal antes = c.getSaldo();
        c.setSaldo(antes.subtract(req.getValor()));
        contaService.salvar(c);
        Transacao t = Transacao.builder().conta(c).tipo(Transacao.TipoTransacao.SAQUE).valor(req.getValor())
            .saldoAntes(antes).saldoDepois(c.getSaldo())
            .descricao(req.getDescricao() != null ? req.getDescricao() : "Saque")
            .status(Transacao.StatusTransacao.CONCLUIDA).build();
        return toResponse(transacaoRepo.save(t), "Saque realizado com sucesso");
    }

    @Transactional
    public TransacaoDTO.TransacaoResponse transferir(String origem, TransacaoDTO.TransferenciaRequest req) {
        validarValor(req.getValor(), "transferência");
        if (origem.equals(req.getNumeroContaDestino())) throw new BankingException.TransferenciaMesmaContaException();
        ContaCorrente cOrig = contaService.buscarEntidadePorNumeroConta(origem);
        ContaCorrente cDest = contaService.buscarEntidadePorNumeroConta(req.getNumeroContaDestino());
        validarAtiva(cOrig); validarAtiva(cDest);
        if (!cOrig.temSaldoSuficiente(req.getValor())) throw new BankingException.SaldoInsuficienteException();
        BigDecimal antOrig = cOrig.getSaldo(), antDest = cDest.getSaldo();
        cOrig.setSaldo(antOrig.subtract(req.getValor()));
        cDest.setSaldo(antDest.add(req.getValor()));
        contaService.salvar(cOrig); contaService.salvar(cDest);
        String desc = req.getDescricao() != null ? req.getDescricao() : "Transferência";
        Transacao tOrig = Transacao.builder().conta(cOrig).numeroContaDestino(req.getNumeroContaDestino())
            .tipo(Transacao.TipoTransacao.TRANSFERENCIA_ENVIADA).valor(req.getValor())
            .saldoAntes(antOrig).saldoDepois(cOrig.getSaldo())
            .descricao(desc + " → " + cDest.getTitular()).status(Transacao.StatusTransacao.CONCLUIDA).build();
        Transacao tDest = Transacao.builder().conta(cDest).numeroContaDestino(origem)
            .tipo(Transacao.TipoTransacao.TRANSFERENCIA_RECEBIDA).valor(req.getValor())
            .saldoAntes(antDest).saldoDepois(cDest.getSaldo())
            .descricao(desc + " ← " + cOrig.getTitular()).status(Transacao.StatusTransacao.CONCLUIDA).build();
        tOrig = transacaoRepo.save(tOrig); transacaoRepo.save(tDest);
        return toResponse(tOrig, "Transferência realizada com sucesso");
    }

    @Transactional(readOnly = true)
    public TransacaoDTO.ExtratoResponse consultarExtrato(String numeroConta) {
        ContaCorrente c = contaService.buscarEntidadePorNumeroConta(numeroConta);
        List<TransacaoDTO.TransacaoResponse> lista = transacaoRepo.findByContaNumeroContaOrderByDataHoraDesc(numeroConta)
            .stream().map(t -> toResponse(t, null)).collect(Collectors.toList());
        return TransacaoDTO.ExtratoResponse.builder().numeroConta(c.getNumeroConta()).titular(c.getTitular())
            .saldoAtual(c.getSaldo()).saldoDisponivel(c.getSaldoDisponivel()).transacoes(lista).dataConsulta(LocalDateTime.now()).build();
    }

    @Transactional(readOnly = true)
    public TransacaoDTO.ExtratoResponse consultarExtratoPorPeriodo(String numeroConta, LocalDateTime inicio, LocalDateTime fim) {
        ContaCorrente c = contaService.buscarEntidadePorNumeroConta(numeroConta);
        List<TransacaoDTO.TransacaoResponse> lista = transacaoRepo.findByContaIdAndPeriodo(c.getId(), inicio, fim)
            .stream().map(t -> toResponse(t, null)).collect(Collectors.toList());
        return TransacaoDTO.ExtratoResponse.builder().numeroConta(c.getNumeroConta()).titular(c.getTitular())
            .saldoAtual(c.getSaldo()).saldoDisponivel(c.getSaldoDisponivel()).transacoes(lista).dataConsulta(LocalDateTime.now()).build();
    }

    private void validarValor(BigDecimal v, String op) {
        if (v == null || v.compareTo(BigDecimal.ZERO) <= 0) throw new BankingException.ValorInvalidoException("Valor de " + op + " deve ser maior que zero");
    }
    private void validarAtiva(ContaCorrente c) {
        if (c.getStatus() != ContaCorrente.StatusConta.ATIVA) throw new BankingException.ContaInativaException(c.getNumeroConta());
    }
    private TransacaoDTO.TransacaoResponse toResponse(Transacao t, String msg) {
        return TransacaoDTO.TransacaoResponse.builder().id(t.getId()).numeroConta(t.getConta().getNumeroConta())
            .numeroContaDestino(t.getNumeroContaDestino()).tipo(t.getTipo()).valor(t.getValor())
            .saldoAntes(t.getSaldoAntes()).saldoDepois(t.getSaldoDepois()).descricao(t.getDescricao())
            .status(t.getStatus()).dataHora(t.getDataHora()).codigoAutorizacao(t.getCodigoAutorizacao()).mensagem(msg).build();
    }
}
