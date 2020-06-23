package com.nabiki.ctp.trader.struct;

public class CThostFtdcInvestorPositionDetailField {
	public String InstrumentID;
	public String BrokerID;
	public String InvestorID;
	public byte HedgeFlag;
	public byte Direction;
	public String OpenDate;
	public String TradeID;
	public int Volume;
	public double OpenPrice;
	public String TradingDay;
	public int SettlementID;
	public byte TradeType;
	public String CombInstrumentID;
	public String ExchangeID;
	public double CloseProfitByDate;
	public double CloseProfitByTrade;
	public double PositionProfitByDate;
	public double PositionProfitByTrade;
	public double Margin;
	public double ExchMargin;
	public double MarginRateByMoney;
	public double MarginRateByVolume;
	public double LastSettlementPrice;
	public double SettlementPrice;
	public int CloseVolume;
	public double CloseAmount;
	public int TimeFirstVolume;
	public String InvestUnitID;

	public CThostFtdcInvestorPositionDetailField() {
	}
}
