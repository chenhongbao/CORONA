package com.nabiki.ctp.md.internal;

import java.util.LinkedList;
import java.util.List;

public class LoginProfile {
	public boolean isUsingUdp = false, isMulticast = false;
	public String FlowPath = "flow";
	public List<String> FrontAddresses = new LinkedList<>();
	
	public LoginProfile() {}
}
