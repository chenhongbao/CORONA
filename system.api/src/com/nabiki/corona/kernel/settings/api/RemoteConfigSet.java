package com.nabiki.corona.kernel.settings.api;

import java.util.Collection;

public interface RemoteConfigSet {
	RemoteConfig config(String name);
	
	Collection<RemoteConfig> configs();
}
