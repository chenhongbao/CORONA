package com.nabiki.corona.system.api;

import java.time.LocalDate;

import com.nabiki.corona.api.Order;

/**
 * Internally used order with full attributes.
 * 
 * @author Hongbao Chen
 *
 */
public abstract class KerOrder extends Order {
	public abstract String brokerId();

	public abstract void brokerId(String s);

	public abstract String investorId();

	public abstract void investorId(String s);

	public abstract String orderId();

	public abstract void orderId(String s);

	public abstract String userId();

	public abstract void userId(String s);

	public abstract LocalDate gtdDate();

	public abstract void gtdDate(LocalDate d);

	public abstract char forceCloseReason();

	public abstract void forceCloseReason(char t);

	public abstract boolean isAutoSuspend();

	public abstract void isAutoSuspend(boolean b);

	public abstract String businessUnit();

	public abstract void businessUnit(String s);

	public abstract int requestId();

	public abstract void requestId(int i);

	public abstract boolean userForceClose();

	public abstract void userForceClose(boolean b);

	public abstract boolean isSwapOrder();

	public abstract void isSwapOrder(boolean b);

	public abstract String exchangeId();

	public abstract void exchangeId(String s);

	public abstract String investUnitId();

	public abstract void investUnitId(String s);

	public abstract String currencyId();

	public abstract void currencyId(String s);

	public abstract String clientId();

	public abstract void clientId(String s);

	public abstract String ipAddress();

	public abstract void ipAddress(String s);

	public abstract String macAddress();

	public abstract void macAddress(String s);
}
