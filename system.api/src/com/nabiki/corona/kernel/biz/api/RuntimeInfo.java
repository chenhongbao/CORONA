package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.Tick;
import com.nabiki.corona.kernel.api.KerCommission;
import com.nabiki.corona.kernel.api.KerInstrument;
import com.nabiki.corona.kernel.api.KerMargin;

public interface RuntimeInfo {
	String name();
	
	boolean ready(String symbol);
	
	void instrument(KerInstrument in);
	
	void margin(KerMargin margin);
	
	void commission(KerCommission comm);
	
	KerInstrument instrument(String symbol);
	
	KerMargin margin(String symbol);
	
	KerCommission commission(String symbol);
	
	Tick lastTick(String symbol);
}
