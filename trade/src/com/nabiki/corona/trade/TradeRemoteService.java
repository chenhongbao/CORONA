package com.nabiki.corona.trade;

import java.time.LocalDate;

import org.osgi.service.component.annotations.Component;

import com.nabiki.corona.api.Order;
import com.nabiki.corona.kernel.biz.api.TradeRemote;

@Component
public class TradeRemoteService implements TradeRemote{

	@Override
	public String name() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String nextOrderId() {
		// TODO get order id
		return null;
	}

	@Override
	public LocalDate tradingDay() {
		// TODO trading day
		return null;
	}

	@Override
	public int order(Order o) {
		// TODO send order
		return 0;
	}

	@Override
	public int instrument(String symbol) {
		// TODO query instrument
		return 0;
	}

	@Override
	public int margin(String symbol) {
		// TODO query margin
		return 0;
	}

	@Override
	public int commission(String symbol) {
		// TODO query commission
		return 0;
	}

	@Override
	public void account() {
		// TODO query account
		
	}

	@Override
	public void position() {
		// TODO query position details
		
	}
	// TODO start trade app.
}
