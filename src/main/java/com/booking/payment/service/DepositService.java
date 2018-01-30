package com.booking.payment.service;

import com.booking.payment.service.dto.DepositDTO;
import java.util.List;

/**
 * Service Interface for managing Deposit.
 */
public interface DepositService {

    /**
     * Save a deposit.
     *
     * @param depositDTO the entity to save
     * @return the persisted entity
     */
    DepositDTO save(DepositDTO depositDTO);

    /**
     * Get all the deposits.
     *
     * @return the list of entities
     */
    List<DepositDTO> findAll();

    /**
     * Get the "id" deposit.
     *
     * @param id the id of the entity
     * @return the entity
     */
    DepositDTO findOne(Long id);

    /**
     * Delete the "id" deposit.
     *
     * @param id the id of the entity
     */
    void delete(Long id);

    /**
     * Search for the deposit corresponding to the query.
     *
     * @param query the query of the search
     * 
     * @return the list of entities
     */
    List<DepositDTO> search(String query);
}
