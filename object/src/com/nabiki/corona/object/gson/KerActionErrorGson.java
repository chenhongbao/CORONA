package com.nabiki.corona.object.gson;

import com.nabiki.corona.system.api.KerAction;
import com.nabiki.corona.system.api.KerActionError;
import com.nabiki.corona.system.api.KerError;

public class KerActionErrorGson implements KerActionError {
	public KerActionGson action;
	public KerError error;
	
	public KerActionErrorGson() {
	}

	@Override
	public KerAction action() {
		return this.action();
	}

	@Override
	public void action(KerAction o) {
		this.action = (KerActionGson)o;
	}

	@Override
	public KerError error() {
		return this.error;
	}

	@Override
	public void error(KerError e) {
		this.error = e;
	}

}
