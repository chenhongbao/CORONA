package com.nabiki.corona.candle.core;

import java.util.Collection;

import com.nabiki.corona.kernel.settings.api.BrokerAccount;
import com.nabiki.corona.kernel.settings.api.NativeExecutableInfo;

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
	public TickEngine(TickEngineListener l, BrokerAccount md, NativeExecutableInfo exec, Collection<String> symbols) {
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
