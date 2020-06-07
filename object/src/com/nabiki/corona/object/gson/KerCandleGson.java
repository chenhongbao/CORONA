package com.nabiki.corona.object.gson;

import java.time.Instant;
import java.time.LocalDate;

import com.nabiki.corona.system.api.KerCandle;

public class KerCandleGson implements KerCandle {
	public String symbol;
	public double openPrice, highPrice, lowPrice, closePrice;
	public int openInterest, volume, minutePeriod;
	public boolean isDay, isLastOfDay, isRealTime;
	public Instant updateTime;
	public LocalDate tradingDay, actionDay;
	
	public KerCandleGson() {}

	@Override
	public String symbol() {
		return this.symbol;
	}

	@Override
	public double openPrice() {
		return this.openPrice;
	}

	@Override
	public double highPrice() {
		return this.highPrice;
	}

	@Override
	public double lowPrice() {
		return this.lowPrice;
	}

	@Override
	public double closePrice() {
		return this.closePrice;
	}

	@Override
	public int openInterest() {
		return this.openInterest;
	}

	@Override
	public int volume() {
		return this.volume;
	}

	@Override
	public int minutePeriod() {
		return this.minutePeriod;
	}

	@Override
	public boolean isDay() {
		return this.isDay;
	}

	@Override
	public boolean isLastOfDay() {
		return this.isLastOfDay;
	}

	@Override
	public boolean isRealTime() {
		return this.isRealTime;
	}

	@Override
	public Instant updateTime() {
		return this.updateTime;
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}

	@Override
	public LocalDate actionDay() {
		return this.actionDay;
	}

	@Override
	public void symbol(String s) {
		this.symbol = s;
	}

	@Override
	public void openPrice(double d) {
		this.openPrice = d;
	}

	@Override
	public void highPrice(double d) {
		this.highPrice = d;
	}

	@Override
	public void lowPrice(double d) {
		this.lowPrice = d;
	}

	@Override
	public void closePrice(double d) {
		this.closePrice = d;
	}

	@Override
	public void openInterest(int i) {
		this.openInterest = i;
	}

	@Override
	public void volume(int i) {
		this.volume = i;
	}

	@Override
	public void minutePeriod(int i) {
		this.minutePeriod = i;
	}

	@Override
	public void isDay(boolean b) {
		this.isDay = b;
	}

	@Override
	public void isLastOfDay(boolean b) {
		this.isLastOfDay = b;
	}

	@Override
	public void isRealTime(boolean b) {
		this.isRealTime = b;
	}

	@Override
	public void updateTime(Instant i) {
		this.updateTime = i;
	}

	@Override
	public void tradingDay(LocalDate d) {
		this.tradingDay = d;
	}

	@Override
	public void actionDay(LocalDate d) {
		this.actionDay = d;
	}

}
