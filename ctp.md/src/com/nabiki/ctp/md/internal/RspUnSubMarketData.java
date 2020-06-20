package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;
import com.nabiki.ctp.md.struct.CThostFtdcSpecificInstrumentField;

public class RspUnSubMarketData {
	public CThostFtdcSpecificInstrumentField SpecificInstrument;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspUnSubMarketData() {
	}
}
