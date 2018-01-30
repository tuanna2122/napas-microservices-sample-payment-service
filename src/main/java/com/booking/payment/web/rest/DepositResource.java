package com.booking.payment.web.rest;

import com.codahale.metrics.annotation.Timed;
import com.booking.payment.service.DepositService;
import com.booking.payment.web.rest.errors.BadRequestAlertException;
import com.booking.payment.web.rest.util.HeaderUtil;
import com.booking.payment.service.dto.DepositDTO;
import io.github.jhipster.web.util.ResponseUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.*;

/**
 * REST controller for managing Deposit.
 */
@RestController
@RequestMapping("/api")
public class DepositResource {

    private final Logger log = LoggerFactory.getLogger(DepositResource.class);

    private static final String ENTITY_NAME = "deposit";

    private final DepositService depositService;

    public DepositResource(DepositService depositService) {
        this.depositService = depositService;
    }

    /**
     * POST  /deposits : Create a new deposit.
     *
     * @param depositDTO the depositDTO to create
     * @return the ResponseEntity with status 201 (Created) and with body the new depositDTO, or with status 400 (Bad Request) if the deposit has already an ID
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PostMapping("/deposits")
    @Timed
    public ResponseEntity<DepositDTO> createDeposit(@RequestBody DepositDTO depositDTO) throws URISyntaxException {
        log.debug("REST request to save Deposit : {}", depositDTO);
        if (depositDTO.getId() != null) {
            throw new BadRequestAlertException("A new deposit cannot already have an ID", ENTITY_NAME, "idexists");
        }
        DepositDTO result = depositService.save(depositDTO);
        return ResponseEntity.created(new URI("/api/deposits/" + result.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(ENTITY_NAME, result.getId().toString()))
            .body(result);
    }

    /**
     * PUT  /deposits : Updates an existing deposit.
     *
     * @param depositDTO the depositDTO to update
     * @return the ResponseEntity with status 200 (OK) and with body the updated depositDTO,
     * or with status 400 (Bad Request) if the depositDTO is not valid,
     * or with status 500 (Internal Server Error) if the depositDTO couldn't be updated
     * @throws URISyntaxException if the Location URI syntax is incorrect
     */
    @PutMapping("/deposits")
    @Timed
    public ResponseEntity<DepositDTO> updateDeposit(@RequestBody DepositDTO depositDTO) throws URISyntaxException {
        log.debug("REST request to update Deposit : {}", depositDTO);
        if (depositDTO.getId() == null) {
            return createDeposit(depositDTO);
        }
        DepositDTO result = depositService.save(depositDTO);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(ENTITY_NAME, depositDTO.getId().toString()))
            .body(result);
    }

    /**
     * GET  /deposits : get all the deposits.
     *
     * @return the ResponseEntity with status 200 (OK) and the list of deposits in body
     */
    @GetMapping("/deposits")
    @Timed
    public List<DepositDTO> getAllDeposits() {
        log.debug("REST request to get all Deposits");
        return depositService.findAll();
        }

    /**
     * GET  /deposits/:id : get the "id" deposit.
     *
     * @param id the id of the depositDTO to retrieve
     * @return the ResponseEntity with status 200 (OK) and with body the depositDTO, or with status 404 (Not Found)
     */
    @GetMapping("/deposits/{id}")
    @Timed
    public ResponseEntity<DepositDTO> getDeposit(@PathVariable Long id) {
        log.debug("REST request to get Deposit : {}", id);
        DepositDTO depositDTO = depositService.findOne(id);
        return ResponseUtil.wrapOrNotFound(Optional.ofNullable(depositDTO));
    }

    /**
     * DELETE  /deposits/:id : delete the "id" deposit.
     *
     * @param id the id of the depositDTO to delete
     * @return the ResponseEntity with status 200 (OK)
     */
    @DeleteMapping("/deposits/{id}")
    @Timed
    public ResponseEntity<Void> deleteDeposit(@PathVariable Long id) {
        log.debug("REST request to delete Deposit : {}", id);
        depositService.delete(id);
        return ResponseEntity.ok().headers(HeaderUtil.createEntityDeletionAlert(ENTITY_NAME, id.toString())).build();
    }

    /**
     * SEARCH  /_search/deposits?query=:query : search for the deposit corresponding
     * to the query.
     *
     * @param query the query of the deposit search
     * @return the result of the search
     */
    @GetMapping("/_search/deposits")
    @Timed
    public List<DepositDTO> searchDeposits(@RequestParam String query) {
        log.debug("REST request to search Deposits for query {}", query);
        return depositService.search(query);
    }

}
