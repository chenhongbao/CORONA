package com.nabiki.ctp.md;

import com.nabiki.ctp.md.internal.LoginProfile;
import com.nabiki.ctp.md.struct.*;

public class CThostFtdcMdApiImpl extends CThostFtdcMdApi {
    private final static String apiVersion = "0.0.1";
    private final LoginProfile profile = new LoginProfile();

    CThostFtdcMdApiImpl(String szFlowPath, boolean isUsingUdp, boolean isMulticast) {
        this.profile.FlowPath = szFlowPath;
        this.profile.isUsingUdp = isUsingUdp;
        this.profile.isMulticast = isMulticast;
    }

    @Override
    public String GetApiVersion() {
        return CThostFtdcMdApiImpl.apiVersion;
    }

    @Override
    public String GetTradingDay() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void Init() {
        // TODO Auto-generated method stub
    }

    @Override
    public void Join() {
        // TODO Auto-generated method stub
    }

    @Override
    public void RegisterFront(String szFrontAddress) {
        // TODO Auto-generated method stub
    }

    @Override
    public void RegisterSpi(CThostFtdcMdSpi spi) {
        // TODO Auto-generated method stub
    }

    @Override
    public void Release() {
        // TODO Auto-generated method stub
    }

    @Override
    public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int SubscribeMarketData(String[] instrumentID, int count) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int UnSubscribeMarketData(String[] instrumentID, int count) {
        // TODO Auto-generated method stub
        return 0;
    }
}
