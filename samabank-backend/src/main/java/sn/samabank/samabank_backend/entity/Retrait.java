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
 * Retrait sur un compte (débit). Le compte débité est le {@code compte} hérité.
 */
@Entity
@Table(name = "retrait")
@Getter
@Setter
@NoArgsConstructor
public class Retrait extends Transaction {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Canal canal;

    @Override
    public TypeTransaction getType() {
        return TypeTransaction.RETRAIT;
    }
}
