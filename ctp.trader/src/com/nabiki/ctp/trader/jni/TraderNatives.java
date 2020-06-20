package com.nabiki.ctp.trader.jni;

import com.nabiki.ctp.trader.internal.*;

public class TraderNatives {
    native public static int CreateChannel();

    native public static void DestroyChannel();

    /**
     * Wait for signal on the specified channel for the specified milliseconds. The method waits on a condition
     * variable until it is signaled or the specified milliseconds elpased, and the returned value has the hints.
     * 
     * <p>It returns {@code 0} on normal signal, or {@code ErrorCodes.NATIVE_TIMEOUT} on timeout.
     * 
     * <p>The method doesn't throw exception.
     * 
     * @param channelId channel ID for the specified channel to wait on
     * @param millis milliseconds to wait before timeout
     * @return {@code 0} for signal, {@code ErrorCodes.NATIVE_TIMEOUT} for timeout
     */
    native public static int WaitOnChannel(int channelId, long millis);

    native public static void SignalChannel(int channelId);

    /**
     * Non-blocking read data from the specified channel. The method reads data currently cached on the specified
     * channel and returns immediately when no data in channel.
     * 
     * <p>The method doesn't intend to throw exception except those caused unexpectedly by JVM internal, and they
     * could be caught as {@link Throwable}.
     * 
     * @param channelId identifier for the specicifed channel to read from
     * @param data read-in channel data
     */
    native public static void ReadChannel(int channelId, TraderChannelData data);

    native public static void WriteChannel(int channelId, TraderChannelData data);

    native public static int CreateTraderSession(LoginProfile profile, int channelId);

    native public static void DestroyTraderSession(int traderSessionId);
    
    native public static int ReqAuthenticate(int traderSessionId, CThostFtdcReqAuthenticateField reqAuthenticateField, int requestId);
	
    native public static int ReqUserLogin(int traderSessionId, CThostFtdcReqUserLoginField reqUserLoginField, int requestId);
	
    native public static int ReqUserLogout(int traderSessionId, CThostFtdcUserLogoutField userLogout, int requestId);
	
    native public static int ReqOrderInsert(int traderSessionId, CThostFtdcInputOrderField inputOrder, int requestId);
	
    native public static int ReqOrderAction(int traderSessionId, CThostFtdcInputOrderActionField inputOrderAction, int requestId);
	
    native public static int ReqQryInstrument(int traderSessionId, CThostFtdcQryInstrumentField qryInstrument, int requestId);
	
    native public static int ReqQryInstrumentCommissionRate(int traderSessionId, CThostFtdcQryInstrumentCommissionRateField qryInstrumentCommissionRate, int requestId);
	
    native public static int ReqQryInstrumentMarginRate(int traderSessionId, CThostFtdcQryInstrumentMarginRateField qryInstrumentMarginRate, int requestId);
	
    native public static int ReqQryTradingAccount(int traderSessionId, CThostFtdcQryTradingAccountField qryTradingAccount, int requestId);
	
    native public static int ReqQryInvestorPositionDetail(int traderSessionId, CThostFtdcQryInvestorPositionDetailField qryInvestorPositionDetail, int requestId);
}
