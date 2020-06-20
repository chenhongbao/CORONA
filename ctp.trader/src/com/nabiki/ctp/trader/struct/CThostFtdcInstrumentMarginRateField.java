package com.nabiki.ctp.trader.struct;

public class CThostFtdcInstrumentMarginRateField {
	public String InstrumentID;
	public byte InvestorRange;
	public String BrokerID;
	public String InvestorID;
	public byte HedgeFlag;
	public double LongMarginRatioByMoney;
	public double LongMarginRatioByVolume;
	public double ShortMarginRatioByMoney;
	public double ShortMarginRatioByVolume;
	public int IsRelative;
	public String ExchangeID;
	public String InvestUnitID;
	
	public CThostFtdcInstrumentMarginRateField() {}
}
