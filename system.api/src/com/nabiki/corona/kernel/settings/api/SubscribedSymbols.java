package com.nabiki.corona.kernel.settings.api;

import java.time.LocalDateTime;
import java.util.Collection;

public interface SubscribedSymbols {
	void updateTime(LocalDateTime now);
	
	void symbols(Collection<String> symbols);
}
