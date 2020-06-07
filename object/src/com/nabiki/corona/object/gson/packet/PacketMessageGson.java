package com.nabiki.corona.object.gson.packet;

import java.time.LocalDateTime;

import com.nabiki.corona.system.api.KerError;

/**
 * The packet class and its sub classes are classes, not interfaces. It is no need to implement them.
 * Just use those classes to access data. However, it needs a wrapper class to hold the data before and after
 * its conversion to and from JSON. So I create classes like {@link PacketMessageGson} and its sub classes.
 * 
 * @author Hongbao¡¡Chen
 *
 */
public class PacketMessageGson {
	public int requestSeq, responseSeq;
	public boolean last;
	public KerError error;
	public LocalDateTime time;
}
