package com.nabiki.corona.system.packet.api;

import java.time.LocalDateTime;
import java.util.Collection;

import com.nabiki.corona.system.api.KerError;

public interface PacketMessage<T> {
	/**
	 * Unique ID denoting the request if it is. Every unique ID is distinguished from the other.
	 * 
	 * @return request id
	 */
	int requestId();
	
	/**
	 * Set request ID for the request if it is.
	 * 
	 * @param id request id
	 */
	void requestId(int id);
	
	/**
	 * Unique ID denoting the response if it is. Every unique ID is distinguished from the other. There
	 * can be multiple responses for the same request.
	 * 
	 * @return response id
	 */
	int responseId();
	
	/**
	 * Set response ID for the response if it is.
	 * 
	 * @param id response id
	 */
	void responseId(int id);
	
	/**
	 * Time stamp when this message is sent.
	 * 
	 * @return time stamp
	 */
	LocalDateTime time();
	
	/**
	 * Set time stamp for this message.
	 * 
	 * @param time time stamp
	 */
	void time(LocalDateTime time);
	
	/**
	 * Error of the response or the very request of the response.
	 * 
	 * @return error
	 */
	KerError error();
	
	/**
	 * Set error on the message.
	 * 
	 * @param e error
	 */
	void error(KerError e);
	
	/**
	 * If there are multiple messages for a request or response, the field marks if it is the last one.
	 * 
	 * @return true if it is the last message for the same request or response, false otherwise
	 */
	boolean last();
	
	/**
	 * Set last mark.
	 * 
	 * @param b true if it is the last message for the same request or response, false otherwise
	 */
	void last(boolean b);
	
	/**
	 * Get collection of all values in payload.
	 * 
	 * @return collection of values
	 */
	Collection<T> values();
	
	/**
	 * Set values into payload.
	 * 
	 * @param values values to set in payload
	 */
	void values(Collection<T> values);
	
	/**
	 * Get element at index, starting from 0 inclusively.
	 * 
	 * @param index element index
	 * @return element at index
	 */
	T value(int index);
	
	/**
	 * Append value to payload.
	 * 
	 * @param value value
	 */
	void value(T value);
	
	/**
	 * Number of elements in payload.
	 * 
	 * @return number
	 */
	int valueCount();
}
