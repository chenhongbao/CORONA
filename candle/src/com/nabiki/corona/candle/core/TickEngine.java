package com.nabiki.corona.candle.core;

import com.nabiki.corona.kernel.settings.api.RuntimeInfo;

public class TickEngine implements Runnable {
	public final static String NATIVE_TITLE = "marketdata";

	private EngineState state = EngineState.STOPPED;

	/**
	 * Tick engine needs the caller to provide listener as tick receiver, account info to login to counter, native
	 * executable path and symbols to subscribe.
	 * 
	 * @param l       tick input listener
	 * @param md      account for market data counter login
	 * @param exec    local executable info
	 * @param symbols symbols to subscribe after login
	 */
	public TickEngine(TickEngineListener l, RuntimeInfo info) {
		// TODO constructor
	}

	@Override
	public void run() {
		// TODO run

	}

	public void tellStopping() {
		this.state = EngineState.STOPPING;
	}

	public EngineState state() {
		return this.state;
	}
}
