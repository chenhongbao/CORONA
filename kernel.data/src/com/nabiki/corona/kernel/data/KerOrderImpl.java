package com.nabiki.corona.kernel.data;

import java.time.LocalDate;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.Utils;
import com.nabiki.corona.kernel.api.KerOrder;

public class KerOrderImpl extends KerOrder {

	public String brokerId;
	public String investorId;
	public String orderId;
	public String userId;
	public char forceCloseReason;
	public boolean isAutoSuspend;
	public String businessUnit;
	public int requestId;
	public boolean userForceClose;
	public boolean isSwapOrder;
	public String exchangeId;
	public String investUnitId;
	public String currencyId;
	public String clientId;
	public String ipAddress;
	public String macAddress;
	
	@JsonbDateFormat("yyyyMMdd")
	public LocalDate gtdDate;

	public KerOrderImpl() {
	}
	
	public KerOrderImpl(KerOrder o) {
		deepCopy(o);
	}

	public void deepCopy(KerOrder o) {
		brokerId(o.brokerId());
		businessUnit(o.businessUnit());
		clientId(o.clientId());
		currencyId(o.currencyId());
		exchangeId(o.exchangeId());
		forceCloseReason(o.forceCloseReason());
		gtdDate(Utils.deepCopy(o.gtdDate()));
		investorId(o.investorId());
		investUnitId(o.investUnitId());
		ipAddress(o.ipAddress());
		isAutoSuspend(o.isAutoSuspend());
		isSwapOrder(o.isSwapOrder());
		macAddress(o.macAddress());
		orderId(o.orderId());
		requestId(o.requestId());
		userForceClose(o.userForceClose());
		userId(o.userId());
	}

	@Override
	public String brokerId() {
		return this.brokerId;
	}

	@Override
	public void brokerId(String s) {
		this.brokerId = s;
	}

	@Override
	public String investorId() {
		return this.investorId;
	}

	@Override
	public void investorId(String s) {
		this.investorId = s;
	}

	@Override
	public String orderId() {
		return this.orderId;
	}

	@Override
	public void orderId(String s) {
		this.orderId = s;
	}

	@Override
	public String userId() {
		return this.userId;
	}

	@Override
	public void userId(String s) {
		this.userId = s;
	}

	@Override
	public LocalDate gtdDate() {
		return this.gtdDate;
	}

	@Override
	public void gtdDate(LocalDate d) {
		this.gtdDate = d;
	}

	@Override
	public char forceCloseReason() {
		return this.forceCloseReason;
	}

	@Override
	public void forceCloseReason(char t) {
		this.forceCloseReason = t;
	}

	@Override
	public boolean isAutoSuspend() {
		return this.isAutoSuspend;
	}

	@Override
	public void isAutoSuspend(boolean b) {
		this.isAutoSuspend = b;
	}

	@Override
	public String businessUnit() {
		return this.businessUnit;
	}

	@Override
	public void businessUnit(String s) {
		this.businessUnit = s;
	}

	@Override
	public int requestId() {
		return this.requestId;
	}

	@Override
	public void requestId(int i) {
		this.requestId = i;
	}

	@Override
	public boolean userForceClose() {
		return this.userForceClose;
	}

	@Override
	public void userForceClose(boolean b) {
		this.userForceClose = b;
	}

	@Override
	public boolean isSwapOrder() {
		return this.isSwapOrder;
	}

	@Override
	public void isSwapOrder(boolean b) {
		this.isSwapOrder = b;
	}

	@Override
	public String exchangeId() {
		return this.exchangeId;
	}

	@Override
	public void exchangeId(String s) {
		this.exchangeId = s;
	}

	@Override
	public String investUnitId() {
		return this.investUnitId;
	}

	@Override
	public void investUnitId(String s) {
		this.investUnitId = s;
	}

	@Override
	public String currencyId() {
		return this.currencyId;
	}

	@Override
	public void currencyId(String s) {
		this.currencyId = s;
	}

	@Override
	public String clientId() {
		return this.clientId;
	}

	@Override
	public void clientId(String s) {
		this.clientId = s;
	}

	@Override
	public String ipAddress() {
		return this.ipAddress;
	}

	@Override
	public void ipAddress(String s) {
		this.ipAddress = s;
	}

	@Override
	public String macAddress() {
		return this.macAddress;
	}

	@Override
	public void macAddress(String s) {
		this.macAddress = s;
	}

}
