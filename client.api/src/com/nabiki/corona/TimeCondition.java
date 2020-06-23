package com.nabiki.corona;

public class TimeCondition {
	/**
	 * 立即完成，否则撤销
	 */
	public static final char IOC = '1';
	
	/**
	 * 本节有效
	 */
	public static final char GFS = '2';
	
	/**
	 * 当日有效
	 */
	public static final char GFD = '3';
	
	/**
	 * 指定日期前有效
	 */
	public static final char GTD = '4';
	
	/**
	 * 撤销前有效
	 */
	public static final char GTC = '5';
	
	/**
	 * 集合竞价有效
	 */
	public static final char GFA = '6';
}
