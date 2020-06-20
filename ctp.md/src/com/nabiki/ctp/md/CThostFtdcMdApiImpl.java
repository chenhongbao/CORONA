package com.nabiki.ctp.md;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

import com.nabiki.ctp.md.internal.*;
import com.nabiki.ctp.md.struct.*;

public class CThostFtdcMdApiImpl extends CThostFtdcMdApi implements AutoCloseable {
    private final static String apiVersion = "0.0.1";
    private final LoginProfile profile = new LoginProfile();
    
	// Front address pattern.
	private Pattern addressRegex = Pattern
			.compile("tcp://((\\d{1})|([1-9]\\d{1,2}))(\\.((\\d{1})|([1-9]\\d{1,2}))){3}:\\d+");
    
    private int sessionId, channelId;
    
    private CThostFtdcMdSpi spi;
    
	// Join/Release.
	private ReentrantLock lock = new ReentrantLock();
	private Condition cond = lock.newCondition();
    
    // Channel reader.
    private MdChannelReader channelReader;
    
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
        return this.channelReader == null ? null : this.channelReader.tradingDay();
    }

    @Override
    public void Init() {
        this.channelId = MdNatives.CreateChannel();
        this.channelReader = new MdChannelReader(this.channelId, this.spi);
        this.sessionId = MdNatives.CreateMdSession(this.profile, this.channelId);
    }

    @Override
    public void Join() {
		while (true)
			try {
				this.cond.await();
				break;
			} catch (InterruptedException e) {
			}
    }

    @Override
    public void RegisterFront(String frontAddress) {
		// Filter mal-formated address.
		if (this.addressRegex.matcher(frontAddress).matches())
			this.profile.FrontAddresses.add(frontAddress);
    }

    @Override
    public void RegisterSpi(CThostFtdcMdSpi spi) {
    	this.spi = spi;
    }

    @Override
    public void Release() {
		MdNatives.DestroyMdSession(this.sessionId);
		this.channelReader.stop();
		this.channelReader = null;
		MdNatives.DestroyChannel(this.channelId);
		// Signal.
		this.cond.signalAll();
    }

    @Override
    public int ReqUserLogin(CThostFtdcReqUserLoginField reqUserLoginField, int requestId) {
        return MdNatives.ReqUserLogin(this.sessionId, reqUserLoginField, requestId);
    }

    @Override
    public int ReqUserLogout(CThostFtdcUserLogoutField userLogout, int requestId) {
        return MdNatives.ReqUserLogout(this.sessionId, userLogout, requestId);
    }

    @Override
    public int SubscribeMarketData(String[] instrumentID, int count) {
        return MdNatives.SubscribeMarketData(this.sessionId, instrumentID, count);
    }

    @Override
    public int UnSubscribeMarketData(String[] instrumentID, int count) {
        return MdNatives.UnSubscribeMarketData(this.sessionId, instrumentID, count);
    }

	@Override
	public void close() throws Exception {
		this.Release();
	}
}
