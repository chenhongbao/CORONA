package com.nabiki.corona.kernel.settings;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.json.bind.annotation.JsonbDateFormat;

import com.nabiki.corona.kernel.settings.api.SubscribedSymbols;

public class SubscribedSymbolsImpl implements SubscribedSymbols {
	@JsonbDateFormat("yyyy-MM-dd HH:mm:ss")
	public LocalDateTime updateTime = LocalDateTime.now();
	public List<String> symbols = new LinkedList<String>();
	
	public SubscribedSymbolsImpl() {
	}

	@Override
	public void updateTime(LocalDateTime now) {
		this.updateTime = now;
	}

	@Override
	public void symbols(Collection<String> symbols) {
		if (!this.symbols.isEmpty())
			this.symbols.clear();
		
		this.symbols.addAll(symbols);
	}

}
