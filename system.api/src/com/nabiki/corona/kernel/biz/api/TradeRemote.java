package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.NewOrder;

public interface TradeRemote {
	String name();

	void newOrder(NewOrder o);
}
