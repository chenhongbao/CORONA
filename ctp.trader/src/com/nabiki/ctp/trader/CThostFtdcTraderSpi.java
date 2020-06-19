package com.nabiki.ctp.trader;

import com.nabiki.ctp.trader.jni.*;

public abstract class CThostFtdcTraderSpi {
    public void OnErrRtnOrderAction(CThostFtdcOrderActionField orderAction, CThostFtdcRspInfoField rspInfo) {}

    public void OnErrRtnOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo) {};

    public  void OnFrontConnected() {};

    public void OnFrontDisconnected(int reason) {};

    public void OnRspAuthenticate(CThostFtdcRspAuthenticateField pRspAuthenticateField, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspError(CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspOrderAction(CThostFtdcInputOrderActionField inputOrderAction, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspOrderInsert(CThostFtdcInputOrderField inputOrder, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspQryInstrument(CThostFtdcInstrumentField instrument, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspQryInstrumentCommissionRate(CThostFtdcInstrumentCommissionRateField instrumentCommissionRate, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspQryInstrumentMarginRate(CThostFtdcInstrumentMarginRateField instrumentMarginRate, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspQryInvestorPositionDetail(CThostFtdcInvestorPositionDetailField investorPositionDetail, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspQryTradingAccount(CThostFtdcTradingAccountField tradingAccount, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspSettlementInfoConfirm(CThostFtdcSettlementInfoConfirmField settlementInfoConfirm, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspUserLogin(CThostFtdcRspUserLoginField rspUserLogin, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspUserLogout(CThostFtdcUserLogoutField userLogout, CThostFtdcRspInfoField rspInfo, int nRequestID, boolean isLast) {};

    public void OnRtnOrder(CThostFtdcOrderField order) {};

    public void OnRtnTrade(CThostFtdcTradeField trade) {};
}
