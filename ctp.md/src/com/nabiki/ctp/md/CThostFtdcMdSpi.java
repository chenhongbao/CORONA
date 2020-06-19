package com.nabiki.ctp.md;

import com.nabiki.ctp.md.jni.*;

public abstract class CThostFtdcMdSpi {
    public  void OnFrontConnected() {};

    public void OnFrontDisconnected(int reason) {};

    public void OnRspError(CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspUserLogin(CThostFtdcRspUserLoginField rspUserLogin, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspUserLogout(CThostFtdcUserLogoutField userLogout, CThostFtdcRspInfoField rspInfo, int nRequestID, boolean isLast) {};

    public void OnRspSubMarketData(CThostFtdcSpecificInstrumentField specificInstrument, CThostFtdcRspInfoField rspInfo, int requestId, boolean isLast) {};

    public void OnRspUnSubMarketData(CThostFtdcSpecificInstrumentField specificInstrument, CThostFtdcRspInfoField rspInfo, int nRequestID, boolean isLast) {};

    public void OnRtnDepthMarketData(CThostFtdcDepthMarketDataField depthMarketData) {};
}
