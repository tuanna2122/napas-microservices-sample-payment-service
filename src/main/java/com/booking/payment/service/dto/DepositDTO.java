package com.booking.payment.service.dto;


import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the Deposit entity.
 */
public class DepositDTO implements Serializable {

    private Long id;

    private String bookingCode;

    private Long userId;

    private String currencyCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBookingCode() {
        return bookingCode;
    }

    public void setBookingCode(String bookingCode) {
        this.bookingCode = bookingCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DepositDTO depositDTO = (DepositDTO) o;
        if(depositDTO.getId() == null || getId() == null) {
            return false;
        }
        return Objects.equals(getId(), depositDTO.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "DepositDTO{" +
            "id=" + getId() +
            ", bookingCode='" + getBookingCode() + "'" +
            ", userId=" + getUserId() +
            ", currencyCode='" + getCurrencyCode() + "'" +
            "}";
    }
}
