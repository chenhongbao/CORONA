package com.nabiki.corona.system.api;

import java.time.LocalDate;

import com.nabiki.corona.client.api.Order;

/**
 * Internally used order with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public abstract class KerOrder extends Order {
	public abstract String orderId();

	public abstract void orderId(String s);

	public abstract LocalDate gtdDate();

	public abstract void gtdDate(LocalDate d);

	public abstract String ipAddress();

	public abstract void ipAddress(String s);

	public abstract String macAddress();

	public abstract void macAddress(String s);
}
