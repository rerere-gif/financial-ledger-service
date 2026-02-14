package com.task.financialledgerservice.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "transaction_date", nullable = false)
    private OffsetDateTime transactionDate;

    @OneToMany(mappedBy = "transaction", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<TransactionEntry> entries = new ArrayList<>();

    public Transaction() {}

    public Transaction(String description, OffsetDateTime transactionDate) {
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public OffsetDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(OffsetDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public List<TransactionEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TransactionEntry> entries) {
        this.entries = entries;
    }

    public void addEntry(TransactionEntry entry) {
        this.entries.add(entry);
        entry.setTransaction(this);
    }

    public void removeEntry(TransactionEntry entry) {
        this.entries.remove(entry);
        entry.setTransaction(null);
    }
}