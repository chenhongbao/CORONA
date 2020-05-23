package com.nabiki.corona.system.info.api;

import java.util.Collection;

public interface RemoteConfigSet {
	RemoteConfig config(String name);
	
	Collection<RemoteConfig> configs();
}
