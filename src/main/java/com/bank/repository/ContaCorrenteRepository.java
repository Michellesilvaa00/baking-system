package com.bank.repository;
import com.bank.model.ContaCorrente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ContaCorrenteRepository extends JpaRepository<ContaCorrente, Long> {
    Optional<ContaCorrente> findByNumeroConta(String numeroConta);
    Optional<ContaCorrente> findByCpf(String cpf);
    boolean existsByNumeroConta(String numeroConta);
    boolean existsByCpf(String cpf);
    List<ContaCorrente> findByStatus(ContaCorrente.StatusConta status);
    @Query("SELECT c FROM ContaCorrente c WHERE c.agencia = :agencia")
    List<ContaCorrente> findByAgencia(@Param("agencia") String agencia);
}
