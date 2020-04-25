package com.nabiki.corona.kernel.biz.api;

import com.nabiki.corona.api.NewOrder;

public interface TradeRemote {
	void onNewOrder(NewOrder o);
}
