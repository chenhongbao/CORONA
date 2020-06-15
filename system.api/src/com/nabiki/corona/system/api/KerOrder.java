package com.nabiki.corona.system.api;

import java.time.LocalDate;

import com.nabiki.corona.client.api.Order;

/**
 * Internally used order with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public interface KerOrder extends Order {
	String sessionId();
	
	void sessionId(String s);
	
	String orderId();

	void orderId(String s);
	
	String accountId();
	
	String symbol();

	LocalDate gtdDate();

	void gtdDate(LocalDate d);

	String ipAddress();

	void ipAddress(String s);

	String macAddress();

	void macAddress(String s);
	
	String note();
}
