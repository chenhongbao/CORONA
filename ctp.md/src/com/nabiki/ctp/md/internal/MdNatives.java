package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcReqUserLoginField;
import com.nabiki.ctp.md.struct.CThostFtdcUserLogoutField;

public class MdNatives {
    native public static int CreateChannel();

    native public static void DestroyChannel(int channelId);

    native public static int WaitOnChannel(int channelId, long millis);

    native public static void SignalChannel(int channelId);

    native public static void ReadChannel(int channelId, MdChannelData data);

    native public static void WriteChannel(int channelId, MdChannelData data);

    native public static int CreateMdSession(LoginProfile profile, int channelId);

    native public static void DestroyMdSession(int mdSessionid);
    
    native public static int ReqUserLogin(int mdSessionid, CThostFtdcReqUserLoginField reqUserLoginField, int requestId);

    native public static int ReqUserLogout(int mdSessionid, CThostFtdcUserLogoutField userLogout, int requestId);

    native public static int SubscribeMarketData(int mdSessionid, String[] instrumentID, int count);

    native public static int UnSubscribeMarketData(int mdSessionid, String[] instrumentID, int count);
}
