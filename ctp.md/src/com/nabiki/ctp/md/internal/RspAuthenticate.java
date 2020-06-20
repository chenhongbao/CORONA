package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcRspAuthenticateField;
import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;

public class RspAuthenticate {
	public CThostFtdcRspAuthenticateField RspAuthenticateField;
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspAuthenticate() {}
}
