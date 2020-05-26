package com.nabiki.corona.portal.core;

import java.nio.file.Path;
import java.util.List;

import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;

public class CandleReader extends CandleFile {

	public CandleReader(Path root) {
		super(root);
	}

	public List<KerCandle> read(String symbol, int period) throws KerError {
		return super.read(symbol, period);
	}
}
