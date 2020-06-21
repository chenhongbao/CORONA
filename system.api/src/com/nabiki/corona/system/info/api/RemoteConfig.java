package com.nabiki.corona.system.info.api;

import java.util.Collection;

public interface RemoteConfig {
	String name();
	
	String flowPath();
	
	Collection<String> addresses();
	
	int publicTopicType();
	
	int privateTopicType();
	
	String userId();
	
	String brokerId();
	
	String password();
	
	String appId();
	
	String authCode();
	
	String userProductInfo();
	
	String currencyId();
}
