package com.bank.controller;
import com.bank.dto.ContaDTO;
import com.bank.model.ContaCorrente;
import com.bank.service.ContaCorrenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/contas")
@RequiredArgsConstructor
public class ContaCorrenteController {
    private final ContaCorrenteService contaService;
    @PostMapping
    public ResponseEntity<ContaDTO.ContaResponse> criarConta(@Valid @RequestBody ContaDTO.CriarContaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(contaService.criarConta(req));
    }
    @GetMapping
    public ResponseEntity<List<ContaDTO.ContaResponse>> listar(@RequestParam(required = false) ContaCorrente.StatusConta status) {
        return ResponseEntity.ok(status != null ? contaService.listarPorStatus(status) : contaService.listarTodas());
    }
    @GetMapping("/{numeroConta}")
    public ResponseEntity<ContaDTO.ContaResponse> buscar(@PathVariable String numeroConta) {
        return ResponseEntity.ok(contaService.buscarPorNumeroConta(numeroConta));
    }
    @GetMapping("/cpf/{cpf}")
    public ResponseEntity<ContaDTO.ContaResponse> buscarPorCpf(@PathVariable String cpf) {
        return ResponseEntity.ok(contaService.buscarPorCpf(cpf));
    }
    @PatchMapping("/{numeroConta}/status")
    public ResponseEntity<ContaDTO.ContaResponse> atualizarStatus(@PathVariable String numeroConta, @Valid @RequestBody ContaDTO.AtualizarStatusRequest req) {
        return ResponseEntity.ok(contaService.atualizarStatus(numeroConta, req.getStatus()));
    }
}
