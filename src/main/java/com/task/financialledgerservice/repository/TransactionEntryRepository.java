package com.task.financialledgerservice.repository;

import com.task.financialledgerservice.model.TransactionEntry;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionEntryRepository extends JpaRepository<TransactionEntry, Long> {

    @EntityGraph(attributePaths = {"transaction"})
    List<TransactionEntry> findByAccountId(Long accountId);
}