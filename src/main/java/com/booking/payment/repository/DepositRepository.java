package com.booking.payment.repository;

import com.booking.payment.domain.Deposit;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.*;


/**
 * Spring Data JPA repository for the Deposit entity.
 */
@SuppressWarnings("unused")
@Repository
public interface DepositRepository extends JpaRepository<Deposit, Long> {

}
