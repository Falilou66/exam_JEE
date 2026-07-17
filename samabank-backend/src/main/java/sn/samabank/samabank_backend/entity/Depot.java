package sn.samabank.samabank_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sn.samabank.samabank_backend.enums.Canal;
import sn.samabank.samabank_backend.enums.TypeTransaction;

/**
 * Dépôt sur un compte (crédit). Le compte crédité est le {@code compte} hérité.
 */
@Entity
@Table(name = "depot")
@Getter
@Setter
@NoArgsConstructor
public class Depot extends Transaction {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Canal canal;

    @Override
    public TypeTransaction getType() {
        return TypeTransaction.DEPOT;
    }
}
