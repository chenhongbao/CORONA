package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerLogin;

public class KerLoginGson implements KerLogin {
	
	public String accountId;
	public String pin;
	public int role;
	public String address;

	public KerLoginGson() {
	}

	@Override
	public String accountId() {
		return this.accountId;
	}

	@Override
	public void accountId(String s) {
		this.accountId = s;
	}

	@Override
	public String pin() {
		return this.pin;
	}

	@Override
	public void pin(String s) {
		this.pin = s;
	}

	@Override
	public int role() {
		return this.role;
	}

	@Override
	public void role(int i) {
		this.role = i;
	}

	@Override
	public String address() {
		return this.address;
	}

	@Override
	public void address(String s) {
		this.address = s;
	}

}
