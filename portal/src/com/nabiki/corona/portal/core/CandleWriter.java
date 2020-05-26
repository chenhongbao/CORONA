package com.nabiki.corona.portal.core;

import java.nio.file.Path;
import java.util.Collection;

import com.nabiki.corona.system.api.KerCandle;
import com.nabiki.corona.system.api.KerError;

public class CandleWriter extends CandleFile {
	public CandleWriter(Path root) {
		super(root);
	}
	
	public void write(Collection<KerCandle> candles) throws KerError {
		if (candles == null)
			return;
		
		for (var c : candles)
			write(c);
	}
}
