package com.bank.exception;
public class BankingException extends RuntimeException {
    private final String codigo;
    public BankingException(String mensagem, String codigo) { super(mensagem); this.codigo = codigo; }
    public String getCodigo() { return codigo; }
    public static class ContaNaoEncontradaException extends BankingException {
        public ContaNaoEncontradaException(String id) { super("Conta não encontrada: " + id, "CONTA_NAO_ENCONTRADA"); }
    }
    public static class SaldoInsuficienteException extends BankingException {
        public SaldoInsuficienteException() { super("Saldo insuficiente para realizar a operação", "SALDO_INSUFICIENTE"); }
    }
    public static class ContaInativaException extends BankingException {
        public ContaInativaException(String n) { super("Conta " + n + " não está ativa", "CONTA_INATIVA"); }
    }
    public static class ValorInvalidoException extends BankingException {
        public ValorInvalidoException(String msg) { super(msg, "VALOR_INVALIDO"); }
    }
    public static class CpfJaCadastradoException extends BankingException {
        public CpfJaCadastradoException(String cpf) { super("CPF já cadastrado: " + cpf, "CPF_JA_CADASTRADO"); }
    }
    public static class TransferenciaMesmaContaException extends BankingException {
        public TransferenciaMesmaContaException() { super("Não é possível transferir para a mesma conta de origem", "TRANSFERENCIA_MESMA_CONTA"); }
    }
}
