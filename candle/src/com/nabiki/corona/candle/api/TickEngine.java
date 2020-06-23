package com.nabiki.corona.candle.api;

import com.nabiki.corona.system.api.KerError;

public interface TickEngine {

	void sendSymbols() throws KerError;

	void stop();

	EngineState state();

	void start() throws KerError;

}