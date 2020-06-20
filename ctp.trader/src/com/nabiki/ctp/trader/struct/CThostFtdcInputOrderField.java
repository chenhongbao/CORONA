package com.nabiki.ctp.trader.struct;

public class CThostFtdcInputOrderField {
	public String BrokerID;
	public String InvestorID;
	public String InstrumentID;
	public String OrderRef;
	public String UserID;
	public byte OrderPriceType;
	public byte Direction;
	public byte	CombOffsetFlag;
	public byte CombHedgeFlag;
	public double LimitPrice;
	public int VolumeTotalOriginal;
	public byte TimeCondition;
	public String GTDDate;
	public byte VolumeCondition;
	public int MinVolume;
	public byte ContingentCondition;
	public double StopPrice;
	public byte ForceCloseReason;
	public int IsAutoSuspend;
	public String BusinessUnit;
	public int RequestID;
	public int UserForceClose;
	public int IsSwapOrder;
	public String ExchangeID;
	public String InvestUnitID;
	public String AccountID;
	public String CurrencyID;
	public String ClientID;
	public String IPAddress;
	public String MacAddress;
	
	public CThostFtdcInputOrderField() {}
}
