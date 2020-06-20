package com.nabiki.ctp.trader.internal;

import java.util.LinkedList;
import java.util.List;

import com.nabiki.ctp.trader.ThostTeResumeType;

public class LoginProfile {
	public String FlowPath = "flow";
	public List<String> FrontAddresses = new LinkedList<>();
	public int PublicTopicType = ThostTeResumeType.THOST_TERT_RESUME;
	public int PrivateTopicType = ThostTeResumeType.THOST_TERT_RESUME;
	
	public LoginProfile() {}
}
