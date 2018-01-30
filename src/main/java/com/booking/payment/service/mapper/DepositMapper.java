package com.booking.payment.service.mapper;

import com.booking.payment.domain.*;
import com.booking.payment.service.dto.DepositDTO;

import org.mapstruct.*;

/**
 * Mapper for the entity Deposit and its DTO DepositDTO.
 */
@Mapper(componentModel = "spring", uses = {})
public interface DepositMapper extends EntityMapper<DepositDTO, Deposit> {



    default Deposit fromId(Long id) {
        if (id == null) {
            return null;
        }
        Deposit deposit = new Deposit();
        deposit.setId(id);
        return deposit;
    }
}
