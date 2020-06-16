package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.CashMove;
import com.nabiki.corona.system.api.CashMoveType;

public class CashMoveGson implements CashMove {
	public String accountId;
	public CashMoveType type;
	public String currencyId;
	public String note;
	public double amount;
	
	public CashMoveGson() {}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public void accountId(String s) {
		this.accountId = s;
	}

	@Override
	public CashMoveType type() {
		return this.type;
	}

	@Override
	public void type(CashMoveType type) {
		this.type = type;
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
	public String note() {
		return this.note;
	}

	@Override
	public void note(String s) {
		this.note = s;
	}

	@Override
	public double amount() {
		return this.amount;
	}

	@Override
	public void amount(double d) {
		this.amount = d;
	}

}
