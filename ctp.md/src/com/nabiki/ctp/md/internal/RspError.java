package com.nabiki.ctp.md.internal;

import com.nabiki.ctp.md.struct.CThostFtdcRspInfoField;

public class RspError {
	public CThostFtdcRspInfoField RspInfo;
	public int RequestId;
	public boolean IsLast;
	
	public RspError() {}
}
