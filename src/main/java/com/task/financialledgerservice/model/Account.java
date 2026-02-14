package com.task.financialledgerservice.model;

import com.task.financialledgerservice.model.enums.AccountType;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "account")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType type;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<TransactionEntry> transactionEntries = new ArrayList<>();

    public Account() {}

    public Account(String name, AccountType type) {
        this.name = name;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AccountType getType() {
        return type;
    }

    public void setType(AccountType type) {
        this.type = type;
    }

    public List<TransactionEntry> getTransactionEntries() {
        return transactionEntries;
    }

    public void setTransactionEntries(List<TransactionEntry> transactionEntries) {
        this.transactionEntries = transactionEntries;
    }

    public void addTransactionEntry(TransactionEntry entry) {
        this.transactionEntries.add(entry);
        entry.setAccount(this);
    }
}
