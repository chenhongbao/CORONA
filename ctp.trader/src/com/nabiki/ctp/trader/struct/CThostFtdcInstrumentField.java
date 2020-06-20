package com.nabiki.ctp.trader.struct;

public class CThostFtdcInstrumentField {
	public String InstrumentID;
	public String ExchangeID;
	public String InstrumentName;
	public String ExchangeInstID;
	public String ProductID;
	public byte ProductClass;
	public int DeliveryYear;
	public int DeliveryMonth;
	public int MaxMarketOrderVolume;
	public int MinMarketOrderVolume;
	public int MaxLimitOrderVolume;
	public int MinLimitOrderVolume;
	public int VolumeMultiple;
	public double PriceTick;
	public String CreateDate;
	public String OpenDate;
	public String ExpireDate;
	public String StartDelivDate;
	public String EndDelivDate;
	public byte InstLifePhase;
	public int IsTrading;
	public byte PositionType;
	public byte PositionDateType;
	public double LongMarginRatio;
	public double ShortMarginRatio;
	public byte MaxMarginSideAlgorithm;
	public String UnderlyingInstrID;
	public double StrikePrice;
	public byte OptionsType;
	public double UnderlyingMultiple;
	public byte CombinationType;
	
	public CThostFtdcInstrumentField() {}
}
