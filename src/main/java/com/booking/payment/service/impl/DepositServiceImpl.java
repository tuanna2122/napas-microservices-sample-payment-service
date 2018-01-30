package com.booking.payment.service.impl;

import com.booking.payment.service.DepositService;
import com.booking.payment.domain.Deposit;
import com.booking.payment.repository.DepositRepository;
import com.booking.payment.repository.search.DepositSearchRepository;
import com.booking.payment.service.dto.DepositDTO;
import com.booking.payment.service.mapper.DepositMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * Service Implementation for managing Deposit.
 */
@Service
@Transactional
public class DepositServiceImpl implements DepositService {

    private final Logger log = LoggerFactory.getLogger(DepositServiceImpl.class);

    private final DepositRepository depositRepository;

    private final DepositMapper depositMapper;

    private final DepositSearchRepository depositSearchRepository;

    public DepositServiceImpl(DepositRepository depositRepository, DepositMapper depositMapper, DepositSearchRepository depositSearchRepository) {
        this.depositRepository = depositRepository;
        this.depositMapper = depositMapper;
        this.depositSearchRepository = depositSearchRepository;
    }

    /**
     * Save a deposit.
     *
     * @param depositDTO the entity to save
     * @return the persisted entity
     */
    @Override
    public DepositDTO save(DepositDTO depositDTO) {
        log.debug("Request to save Deposit : {}", depositDTO);
        Deposit deposit = depositMapper.toEntity(depositDTO);
        deposit = depositRepository.save(deposit);
        DepositDTO result = depositMapper.toDto(deposit);
        depositSearchRepository.save(deposit);
        return result;
    }

    /**
     * Get all the deposits.
     *
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<DepositDTO> findAll() {
        log.debug("Request to get all Deposits");
        return depositRepository.findAll().stream()
            .map(depositMapper::toDto)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    /**
     * Get one deposit by id.
     *
     * @param id the id of the entity
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public DepositDTO findOne(Long id) {
        log.debug("Request to get Deposit : {}", id);
        Deposit deposit = depositRepository.findOne(id);
        return depositMapper.toDto(deposit);
    }

    /**
     * Delete the deposit by id.
     *
     * @param id the id of the entity
     */
    @Override
    public void delete(Long id) {
        log.debug("Request to delete Deposit : {}", id);
        depositRepository.delete(id);
        depositSearchRepository.delete(id);
    }

    /**
     * Search for the deposit corresponding to the query.
     *
     * @param query the query of the search
     * @return the list of entities
     */
    @Override
    @Transactional(readOnly = true)
    public List<DepositDTO> search(String query) {
        log.debug("Request to search Deposits for query {}", query);
        return StreamSupport
            .stream(depositSearchRepository.search(queryStringQuery(query)).spliterator(), false)
            .map(depositMapper::toDto)
            .collect(Collectors.toList());
    }
}
