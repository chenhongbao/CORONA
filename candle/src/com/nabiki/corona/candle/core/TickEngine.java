package com.nabiki.corona.candle.core;

import com.nabiki.corona.kernel.settings.api.BrokerAccount;
import com.nabiki.corona.kernel.settings.api.NativeExecutableInfo;

public class TickEngine implements Runnable {
	//Tick engine state.
	private enum EngineState {
		STARTING, STARTED, STOPPING, STOPPED
	}
	
	private EngineState state = EngineState.STOPPED;

	public TickEngine(TickEngineListener l, BrokerAccount trade, BrokerAccount md, NativeExecutableInfo exec) {
		// TODO constructor
	}

	@Override
	public void run() {
		// TODO run

	}

	public void stopping() {
		this.state = EngineState.STOPPING;
	}
}
