package com.booking.payment.repository.search;

import com.booking.payment.domain.Deposit;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Spring Data Elasticsearch repository for the Deposit entity.
 */
public interface DepositSearchRepository extends ElasticsearchRepository<Deposit, Long> {
}
