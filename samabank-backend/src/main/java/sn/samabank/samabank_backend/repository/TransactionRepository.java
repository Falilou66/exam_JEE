package sn.samabank.samabank_backend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import sn.samabank.samabank_backend.entity.Transaction;
import sn.samabank.samabank_backend.enums.StatutTransaction;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    boolean existsByReference(String reference);

    /** Transactions d'un statut donné (ex. opérations en attente de validation). */
    Page<Transaction> findByStatut(StatutTransaction statut, Pageable pageable);

    /** Transactions d'un compte sur une période (relevé, SEQ-05), ordonnées. */
    @Query("""
            select t from Transaction t
            where (t.compte.id = :compteId
                   or (type(t) = Virement and treat(t as Virement).compteDestination.id = :compteId))
              and t.dateOperation >= :debut and t.dateOperation < :fin
            order by t.dateOperation asc
            """)
    List<Transaction> findReleve(@Param("compteId") Long compteId,
            @Param("debut") LocalDateTime debut,
            @Param("fin") LocalDateTime fin);

    /**
     * Historique d'un compte (SEQ-04) : toutes les transactions où le compte est
     * concerné, y compris les virements dont il est la destination.
     */
    @Query(value = """
            select t from Transaction t
            where t.compte.id = :compteId
               or (type(t) = Virement and treat(t as Virement).compteDestination.id = :compteId)
            """,
            countQuery = """
            select count(t) from Transaction t
            where t.compte.id = :compteId
               or (type(t) = Virement and treat(t as Virement).compteDestination.id = :compteId)
            """)
    Page<Transaction> findHistorique(@Param("compteId") Long compteId, Pageable pageable);
}
