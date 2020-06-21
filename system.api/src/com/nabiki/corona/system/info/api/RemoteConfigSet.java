package com.nabiki.corona.system.info.api;

import java.util.Collection;

public interface RemoteConfigSet {
	RemoteConfig config(String name);
	
	RemoteConfig traderConfig();
	
	RemoteConfig mdConfig();
	
	Collection<RemoteConfig> configs();
}
