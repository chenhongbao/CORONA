package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerNewAccount;

public class KerNewAccountGson implements KerNewAccount {
	
	public String accountId;
	public String pin;
	public int role;

	public KerNewAccountGson() {
	}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public void accountId(String id) {
		this.accountId = id;
	}

	@Override
	public String pin() {
		return this.pin;
	}

	@Override
	public void pin(String pin) {
		this.pin = pin;
	}

	@Override
	public int role() {
		return this.role;
	}

	@Override
	public void role(int r) {
		this.role = r;
	}

}
