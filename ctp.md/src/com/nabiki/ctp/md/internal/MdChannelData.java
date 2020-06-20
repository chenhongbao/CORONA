package com.nabiki.ctp.md.internal;

import java.util.List;

import com.nabiki.ctp.md.struct.CThostFtdcConnect;
import com.nabiki.ctp.md.struct.CThostFtdcDepthMarketDataField;
import com.nabiki.ctp.md.struct.CThostFtdcDisconnect;

public class MdChannelData {
	public List<CThostFtdcConnect> ListConnect;
	
	public List<CThostFtdcDisconnect> ListDisconnect;
	
	public List<RspAuthenticate> ListRspAuthenticate;
	
	public List<RspError> ListRspError;
	
	public List<RspUserLogin> ListRspUserLogin;
	
	public List<RspUserLogout> ListRspUserLogout;
	
	public List<CThostFtdcDepthMarketDataField> ListRtnDepthMarketData;
}
