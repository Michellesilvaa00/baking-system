package com.bank.controller;
import com.bank.dto.TransacaoDTO;
import com.bank.service.TransacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/contas/{numeroConta}")
@RequiredArgsConstructor
public class TransacaoController {
    private final TransacaoService transacaoService;
    @PostMapping("/depositar")
    public ResponseEntity<TransacaoDTO.TransacaoResponse> depositar(@PathVariable String numeroConta, @Valid @RequestBody TransacaoDTO.DepositoRequest req) {
        return ResponseEntity.ok(transacaoService.depositar(numeroConta, req));
    }
    @PostMapping("/sacar")
    public ResponseEntity<TransacaoDTO.TransacaoResponse> sacar(@PathVariable String numeroConta, @Valid @RequestBody TransacaoDTO.SaqueRequest req) {
        return ResponseEntity.ok(transacaoService.sacar(numeroConta, req));
    }
    @PostMapping("/transferir")
    public ResponseEntity<TransacaoDTO.TransacaoResponse> transferir(@PathVariable String numeroConta, @Valid @RequestBody TransacaoDTO.TransferenciaRequest req) {
        return ResponseEntity.ok(transacaoService.transferir(numeroConta, req));
    }
    @GetMapping("/extrato")
    public ResponseEntity<TransacaoDTO.ExtratoResponse> extrato(@PathVariable String numeroConta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        if (inicio != null && fim != null) return ResponseEntity.ok(transacaoService.consultarExtratoPorPeriodo(numeroConta, inicio, fim));
        return ResponseEntity.ok(transacaoService.consultarExtrato(numeroConta));
    }
}
