package com.booking.payment.web.rest;

import com.booking.payment.PaymentServiceApp;

import com.booking.payment.domain.Deposit;
import com.booking.payment.repository.DepositRepository;
import com.booking.payment.service.DepositService;
import com.booking.payment.repository.search.DepositSearchRepository;
import com.booking.payment.service.dto.DepositDTO;
import com.booking.payment.service.mapper.DepositMapper;
import com.booking.payment.web.rest.errors.ExceptionTranslator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;

import static com.booking.payment.web.rest.TestUtil.createFormattingConversionService;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for the DepositResource REST controller.
 *
 * @see DepositResource
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = PaymentServiceApp.class)
public class DepositResourceIntTest {

    private static final String DEFAULT_BOOKING_CODE = "AAAAAAAAAA";
    private static final String UPDATED_BOOKING_CODE = "BBBBBBBBBB";

    private static final Long DEFAULT_USER_ID = 1L;
    private static final Long UPDATED_USER_ID = 2L;

    private static final String DEFAULT_CURRENCY_CODE = "AAAAAAAAAA";
    private static final String UPDATED_CURRENCY_CODE = "BBBBBBBBBB";

    @Autowired
    private DepositRepository depositRepository;

    @Autowired
    private DepositMapper depositMapper;

    @Autowired
    private DepositService depositService;

    @Autowired
    private DepositSearchRepository depositSearchRepository;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Autowired
    private ExceptionTranslator exceptionTranslator;

    @Autowired
    private EntityManager em;

    private MockMvc restDepositMockMvc;

    private Deposit deposit;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        final DepositResource depositResource = new DepositResource(depositService);
        this.restDepositMockMvc = MockMvcBuilders.standaloneSetup(depositResource)
            .setCustomArgumentResolvers(pageableArgumentResolver)
            .setControllerAdvice(exceptionTranslator)
            .setConversionService(createFormattingConversionService())
            .setMessageConverters(jacksonMessageConverter).build();
    }

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Deposit createEntity(EntityManager em) {
        Deposit deposit = new Deposit()
            .bookingCode(DEFAULT_BOOKING_CODE)
            .userId(DEFAULT_USER_ID)
            .currencyCode(DEFAULT_CURRENCY_CODE);
        return deposit;
    }

    @Before
    public void initTest() {
        depositSearchRepository.deleteAll();
        deposit = createEntity(em);
    }

    @Test
    @Transactional
    public void createDeposit() throws Exception {
        int databaseSizeBeforeCreate = depositRepository.findAll().size();

        // Create the Deposit
        DepositDTO depositDTO = depositMapper.toDto(deposit);
        restDepositMockMvc.perform(post("/api/deposits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(depositDTO)))
            .andExpect(status().isCreated());

        // Validate the Deposit in the database
        List<Deposit> depositList = depositRepository.findAll();
        assertThat(depositList).hasSize(databaseSizeBeforeCreate + 1);
        Deposit testDeposit = depositList.get(depositList.size() - 1);
        assertThat(testDeposit.getBookingCode()).isEqualTo(DEFAULT_BOOKING_CODE);
        assertThat(testDeposit.getUserId()).isEqualTo(DEFAULT_USER_ID);
        assertThat(testDeposit.getCurrencyCode()).isEqualTo(DEFAULT_CURRENCY_CODE);

        // Validate the Deposit in Elasticsearch
        Deposit depositEs = depositSearchRepository.findOne(testDeposit.getId());
        assertThat(depositEs).isEqualToIgnoringGivenFields(testDeposit);
    }

    @Test
    @Transactional
    public void createDepositWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = depositRepository.findAll().size();

        // Create the Deposit with an existing ID
        deposit.setId(1L);
        DepositDTO depositDTO = depositMapper.toDto(deposit);

        // An entity with an existing ID cannot be created, so this API call must fail
        restDepositMockMvc.perform(post("/api/deposits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(depositDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Deposit in the database
        List<Deposit> depositList = depositRepository.findAll();
        assertThat(depositList).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    public void getAllDeposits() throws Exception {
        // Initialize the database
        depositRepository.saveAndFlush(deposit);

        // Get all the depositList
        restDepositMockMvc.perform(get("/api/deposits?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deposit.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookingCode").value(hasItem(DEFAULT_BOOKING_CODE.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE.toString())));
    }

    @Test
    @Transactional
    public void getDeposit() throws Exception {
        // Initialize the database
        depositRepository.saveAndFlush(deposit);

        // Get the deposit
        restDepositMockMvc.perform(get("/api/deposits/{id}", deposit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.id").value(deposit.getId().intValue()))
            .andExpect(jsonPath("$.bookingCode").value(DEFAULT_BOOKING_CODE.toString()))
            .andExpect(jsonPath("$.userId").value(DEFAULT_USER_ID.intValue()))
            .andExpect(jsonPath("$.currencyCode").value(DEFAULT_CURRENCY_CODE.toString()));
    }

    @Test
    @Transactional
    public void getNonExistingDeposit() throws Exception {
        // Get the deposit
        restDepositMockMvc.perform(get("/api/deposits/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateDeposit() throws Exception {
        // Initialize the database
        depositRepository.saveAndFlush(deposit);
        depositSearchRepository.save(deposit);
        int databaseSizeBeforeUpdate = depositRepository.findAll().size();

        // Update the deposit
        Deposit updatedDeposit = depositRepository.findOne(deposit.getId());
        // Disconnect from session so that the updates on updatedDeposit are not directly saved in db
        em.detach(updatedDeposit);
        updatedDeposit
            .bookingCode(UPDATED_BOOKING_CODE)
            .userId(UPDATED_USER_ID)
            .currencyCode(UPDATED_CURRENCY_CODE);
        DepositDTO depositDTO = depositMapper.toDto(updatedDeposit);

        restDepositMockMvc.perform(put("/api/deposits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(depositDTO)))
            .andExpect(status().isOk());

        // Validate the Deposit in the database
        List<Deposit> depositList = depositRepository.findAll();
        assertThat(depositList).hasSize(databaseSizeBeforeUpdate);
        Deposit testDeposit = depositList.get(depositList.size() - 1);
        assertThat(testDeposit.getBookingCode()).isEqualTo(UPDATED_BOOKING_CODE);
        assertThat(testDeposit.getUserId()).isEqualTo(UPDATED_USER_ID);
        assertThat(testDeposit.getCurrencyCode()).isEqualTo(UPDATED_CURRENCY_CODE);

        // Validate the Deposit in Elasticsearch
        Deposit depositEs = depositSearchRepository.findOne(testDeposit.getId());
        assertThat(depositEs).isEqualToIgnoringGivenFields(testDeposit);
    }

    @Test
    @Transactional
    public void updateNonExistingDeposit() throws Exception {
        int databaseSizeBeforeUpdate = depositRepository.findAll().size();

        // Create the Deposit
        DepositDTO depositDTO = depositMapper.toDto(deposit);

        // If the entity doesn't have an ID, it will be created instead of just being updated
        restDepositMockMvc.perform(put("/api/deposits")
            .contentType(TestUtil.APPLICATION_JSON_UTF8)
            .content(TestUtil.convertObjectToJsonBytes(depositDTO)))
            .andExpect(status().isCreated());

        // Validate the Deposit in the database
        List<Deposit> depositList = depositRepository.findAll();
        assertThat(depositList).hasSize(databaseSizeBeforeUpdate + 1);
    }

    @Test
    @Transactional
    public void deleteDeposit() throws Exception {
        // Initialize the database
        depositRepository.saveAndFlush(deposit);
        depositSearchRepository.save(deposit);
        int databaseSizeBeforeDelete = depositRepository.findAll().size();

        // Get the deposit
        restDepositMockMvc.perform(delete("/api/deposits/{id}", deposit.getId())
            .accept(TestUtil.APPLICATION_JSON_UTF8))
            .andExpect(status().isOk());

        // Validate Elasticsearch is empty
        boolean depositExistsInEs = depositSearchRepository.exists(deposit.getId());
        assertThat(depositExistsInEs).isFalse();

        // Validate the database is empty
        List<Deposit> depositList = depositRepository.findAll();
        assertThat(depositList).hasSize(databaseSizeBeforeDelete - 1);
    }

    @Test
    @Transactional
    public void searchDeposit() throws Exception {
        // Initialize the database
        depositRepository.saveAndFlush(deposit);
        depositSearchRepository.save(deposit);

        // Search the deposit
        restDepositMockMvc.perform(get("/api/_search/deposits?query=id:" + deposit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(deposit.getId().intValue())))
            .andExpect(jsonPath("$.[*].bookingCode").value(hasItem(DEFAULT_BOOKING_CODE.toString())))
            .andExpect(jsonPath("$.[*].userId").value(hasItem(DEFAULT_USER_ID.intValue())))
            .andExpect(jsonPath("$.[*].currencyCode").value(hasItem(DEFAULT_CURRENCY_CODE.toString())));
    }

    @Test
    @Transactional
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Deposit.class);
        Deposit deposit1 = new Deposit();
        deposit1.setId(1L);
        Deposit deposit2 = new Deposit();
        deposit2.setId(deposit1.getId());
        assertThat(deposit1).isEqualTo(deposit2);
        deposit2.setId(2L);
        assertThat(deposit1).isNotEqualTo(deposit2);
        deposit1.setId(null);
        assertThat(deposit1).isNotEqualTo(deposit2);
    }

    @Test
    @Transactional
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(DepositDTO.class);
        DepositDTO depositDTO1 = new DepositDTO();
        depositDTO1.setId(1L);
        DepositDTO depositDTO2 = new DepositDTO();
        assertThat(depositDTO1).isNotEqualTo(depositDTO2);
        depositDTO2.setId(depositDTO1.getId());
        assertThat(depositDTO1).isEqualTo(depositDTO2);
        depositDTO2.setId(2L);
        assertThat(depositDTO1).isNotEqualTo(depositDTO2);
        depositDTO1.setId(null);
        assertThat(depositDTO1).isNotEqualTo(depositDTO2);
    }

    @Test
    @Transactional
    public void testEntityFromId() {
        assertThat(depositMapper.fromId(42L).getId()).isEqualTo(42);
        assertThat(depositMapper.fromId(null)).isNull();
    }
}
