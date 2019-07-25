package io.basquiat.bnb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import io.basquiat.bnb.domain.entity.BlockTransactions;

/**
 * 
 * 
 * BlockTransactionsRepository
 * 
 * Mapping Table : block_transactions
 * 
 * created by basquiat
 *
 */
public interface BlockTransactionsRepository extends JpaRepository<BlockTransactions, Integer>{

}
