package com.bank.exception;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BankingException.ContaNaoEncontradaException.class)
    public ResponseEntity<ErroResponse> handleNaoEncontrada(BankingException.ContaNaoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErroResponse.of(ex.getCodigo(), ex.getMessage()));
    }
    @ExceptionHandler(BankingException.SaldoInsuficienteException.class)
    public ResponseEntity<ErroResponse> handleSaldo(BankingException.SaldoInsuficienteException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ErroResponse.of(ex.getCodigo(), ex.getMessage()));
    }
    @ExceptionHandler(BankingException.ContaInativaException.class)
    public ResponseEntity<ErroResponse> handleInativa(BankingException.ContaInativaException ex) {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(ErroResponse.of(ex.getCodigo(), ex.getMessage()));
    }
    @ExceptionHandler(BankingException.CpfJaCadastradoException.class)
    public ResponseEntity<ErroResponse> handleCpf(BankingException.CpfJaCadastradoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ErroResponse.of(ex.getCodigo(), ex.getMessage()));
    }
    @ExceptionHandler(BankingException.class)
    public ResponseEntity<ErroResponse> handleBanking(BankingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErroResponse.of(ex.getCodigo(), ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> erros = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(e -> erros.put(((FieldError)e).getField(), e.getDefaultMessage()));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErroResponse.ofValidation("VALIDACAO_FALHOU", "Erro de validação", erros));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErroResponse.of("ERRO_INTERNO", "Erro interno: " + ex.getMessage()));
    }
    public record ErroResponse(String codigo, String mensagem, Map<String,String> detalhes, LocalDateTime timestamp) {
        static ErroResponse of(String c, String m) { return new ErroResponse(c, m, null, LocalDateTime.now()); }
        static ErroResponse ofValidation(String c, String m, Map<String,String> d) { return new ErroResponse(c, m, d, LocalDateTime.now()); }
    }
}
