package com.nabiki.ctp.trader.struct;

public class CThostFtdcInstrumentCommissionRateField {
	public String InstrumentID;
	public byte InvestorRange;
	public String BrokerID;
	public String InvestorID;
	public double OpenRatioByMoney;
	public double OpenRatioByVolume;
	public double CloseRatioByMoney;
	public double CloseRatioByVolume;
	public double CloseTodayRatioByMoney;
	public double CloseTodayRatioByVolume;
	public String ExchangeID;
	public byte BizType;
	public String InvestUnitID;

	public CThostFtdcInstrumentCommissionRateField() {
	}
}
