package com.nabiki.ctp.trader.internal;

import com.nabiki.ctp.trader.struct.CThostFtdcInputOrderActionField;
import com.nabiki.ctp.trader.struct.CThostFtdcInputOrderField;
import com.nabiki.ctp.trader.struct.CThostFtdcQryInstrumentCommissionRateField;
import com.nabiki.ctp.trader.struct.CThostFtdcQryInstrumentField;
import com.nabiki.ctp.trader.struct.CThostFtdcQryInstrumentMarginRateField;
import com.nabiki.ctp.trader.struct.CThostFtdcQryInvestorPositionDetailField;
import com.nabiki.ctp.trader.struct.CThostFtdcQryTradingAccountField;
import com.nabiki.ctp.trader.struct.CThostFtdcReqAuthenticateField;
import com.nabiki.ctp.trader.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp.trader.struct.CThostFtdcUserLogoutField;

public class TraderNatives {
	/**
	 * Create native data channel whose identifier is then returned.
	 * <p>The method doesn't throw exception.
	 * 
	 * @return identifier for the new channel
	 */
    native public static int CreateChannel();

    /**
     * Destroy the specified channel. If the channel with given channel ID doesn't exist, nothing happens.
     * 
     * <p>The method doesn't throw exception.
     * 
     * @param channelId identifier for the channel to be destroyed
     */
    native public static void DestroyChannel(int channelId);

    /**
     * Wait for signal on the specified channel for the specified milliseconds. The method waits on a condition
     * variable until it is signaled or the specified milliseconds elpased, and the returned value has the hints.
     * 
     * <p>It returns {@code 0} on normal signal, or {@code ErrorCodes.NATIVE_TIMEOUT} on timeout.
     * 
     * <p>If channel with the given identifier doesn't exist, nothing happens. The method doesn't throw exception.
     * 
     * @param channelId channel ID for the specified channel to wait on
     * @param millis milliseconds to wait before timeout
     * @return {@code 0} for signal, {@code ErrorCodes.NATIVE_TIMEOUT} for timeout
     */
    native public static int WaitOnChannel(int channelId, long millis);

    /**
     * Signal the specified channel. If channel with the given identifier doesn't exist, nothing happens.
     * The method doesn't throw exception.
     * 
     * @param channelId identifier for the specified channel
     */
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

    /**
     * Write the specified data to the specified channel. Like its counter-part, the method doesn't throw exception
	 * except those by JVM internal.
     * 
     * @param channelId identifier for the specified channel
     * @param data data to be writen to the channel
     */
    native public static void WriteChannel(int channelId, TraderChannelData data);

    /**
     * Create a trader connection to remote counter with the specified profile and associated the session with the
     * specified channel. Data from remote counter is read from the specified channel.
     * 
     * <p>The method validates the profile and throws exception on incorrect profile.
     * 
     * @param profile the profile that has initialization parameters for trader counter
     * @param channelId the channel associated with this new trader session
     * @return identifier for the new session
     * @throws IllegalFrontAddressException if there exists illegal front address that is not like {@code tcp://127.0.0.1:40001}
     * @throws IllegalSubscribeTopicTypeException if either type of public or private topic is incorrect
     */
    native public static int CreateTraderSession(LoginProfile profile, int channelId);

    /**
     * Destroy the specified trader session. The method disconnects the native session from remote counter and this
     * results in automatic logout from the remote. If the session with given ID doesn't exist, nothing happens.
     * 
     * <p>For a better behavior, a logout request before destroying the session is prefered.
     * 
     * @param traderSessionId identifier for the trader session to be destroyed
     */
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
