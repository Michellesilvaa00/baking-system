package com.bank.repository;
import com.bank.model.Transacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransacaoRepository extends JpaRepository<Transacao, Long> {
    List<Transacao> findByContaIdOrderByDataHoraDesc(Long contaId);
    List<Transacao> findByContaNumeroContaOrderByDataHoraDesc(String numeroConta);
    @Query("SELECT t FROM Transacao t WHERE t.conta.id = :contaId AND t.dataHora BETWEEN :inicio AND :fim ORDER BY t.dataHora DESC")
    List<Transacao> findByContaIdAndPeriodo(@Param("contaId") Long contaId, @Param("inicio") LocalDateTime inicio, @Param("fim") LocalDateTime fim);
}
