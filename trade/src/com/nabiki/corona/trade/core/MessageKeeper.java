package com.nabiki.corona.trade.core;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.nabiki.corona.system.api.KerError;

public class MessageKeeper<T> {
	private final Map<String, List<T>> map = new ConcurrentHashMap<>();
	
	public MessageKeeper() {}
	
	public void message(String sid, T rep) throws KerError {
		if (sid == null || sid.length() == 0)
			throw new KerError("Invalid session ID.");
		
		var l = this.map.get(sid);
		if (l == null)
			this.map.put(sid, new LinkedList<T>());
		// Sync on the linked list.
		var list = this.map.get(sid);
		synchronized (list) {
			list.add(rep);
		}
	}
	
	public List<T> messages(String sid) throws KerError {
		if (sid == null)
			throw new KerError("Invalid parameter, session ID nulll pointer.");
		// Sync on the linked list.
		var list = this.map.get(sid);
		if (list == null)
			return new LinkedList<T>();
		
		synchronized (list) {
			return new LinkedList<T>(list);
		}
	}
}
