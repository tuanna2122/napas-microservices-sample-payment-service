/**
 * 
 */
package com.booking.payment.web.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.booking.payment.config.NapasGatewayProperties;

/**
 * @author Tuan Nguyen
 *
 */
@RestController
@RequestMapping("/api")
public class PaymentResource {

	@Autowired
	private NapasGatewayProperties gatewayProperties;
	
	@GetMapping("/gateway-properties")
	public NapasGatewayProperties gatewayProperties() {
		return this.gatewayProperties;
	}
	
}
