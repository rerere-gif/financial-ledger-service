package com.task.financialledgerservice.model;

import com.task.financialledgerservice.model.enums.EntryType;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "transaction_entry")
public class TransactionEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @NamedEntityGraph(
            name = "TransactionEntry.withTransaction",
            attributeNodes = @NamedAttributeNode("transaction")
    )
    public static class NamedEntityGraphs {}

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(name = "entry_type", nullable = false)
    private EntryType type;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    public TransactionEntry() {}

    public TransactionEntry(Transaction transaction, Account account, EntryType type, BigDecimal amount) {
        this.transaction = transaction;
        this.account = account;
        this.type = type;
        this.amount = amount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public EntryType getType() {
        return type;
    }

    public void setType(EntryType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}