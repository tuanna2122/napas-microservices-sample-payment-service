/**
 * 
 */
package com.booking.payment.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author Tuan Nguyen
 *
 */
@ConfigurationProperties(prefix = "napas.gateway", ignoreUnknownFields = false)
public class NapasGatewayProperties {

	private String vpcUrl;
	private String merchantId;
	private String accessCode;
	private String secureHash;
	private String username;
	private String password;

	public String getVpcUrl() {
		return vpcUrl;
	}

	public String getMerchantId() {
		return merchantId;
	}

	public String getAccessCode() {
		return accessCode;
	}

	public String getSecureHash() {
		return secureHash;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public void setVpcUrl(String vpcUrl) {
		this.vpcUrl = vpcUrl;
	}

	public void setMerchantId(String merchantId) {
		this.merchantId = merchantId;
	}

	public void setAccessCode(String accessCode) {
		this.accessCode = accessCode;
	}

	public void setSecureHash(String secureHash) {
		this.secureHash = secureHash;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
