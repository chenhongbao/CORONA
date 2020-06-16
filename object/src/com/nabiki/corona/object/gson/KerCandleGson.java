package com.nabiki.corona.object.gson;

import java.time.Instant;
import java.time.LocalDate;

import com.nabiki.corona.system.api.KerCandle;

public class KerCandleGson implements KerCandle {
	public boolean isDay;
	public boolean isLastOfDay;
	public boolean isRealTime;
	public int openInterest;
	public int volume;
	public int minutePeriod;
	public double openPrice;
	public double highPrice;
	public double lowPrice;
	public double closePrice;
	public String symbol;
	public LocalDate tradingDay;
	public LocalDate actionDay;
	public Instant updateTime;
	
	public KerCandleGson() {}

	@Override
	public LocalDate actionDay() {
		return this.actionDay;
	}

	@Override
	public void actionDay(LocalDate d) {
		this.actionDay = d;
	}

	@Override
	public double closePrice() {
		return this.closePrice;
	}

	@Override
	public void closePrice(double d) {
		this.closePrice = d;
	}

	@Override
	public double highPrice() {
		return this.highPrice;
	}

	@Override
	public void highPrice(double d) {
		this.highPrice = d;
	}

	@Override
	public boolean isDay() {
		return this.isDay;
	}

	@Override
	public void isDay(boolean b) {
		this.isDay = b;
	}

	@Override
	public boolean isLastOfDay() {
		return this.isLastOfDay;
	}

	@Override
	public void isLastOfDay(boolean b) {
		this.isLastOfDay = b;
	}

	@Override
	public boolean isRealTime() {
		return this.isRealTime;
	}

	@Override
	public void isRealTime(boolean b) {
		this.isRealTime = b;
	}

	@Override
	public double lowPrice() {
		return this.lowPrice;
	}

	@Override
	public void lowPrice(double d) {
		this.lowPrice = d;
	}

	@Override
	public int minutePeriod() {
		return this.minutePeriod;
	}

	@Override
	public void minutePeriod(int i) {
		this.minutePeriod = i;
	}

	@Override
	public int openInterest() {
		return this.openInterest;
	}

	@Override
	public void openInterest(int i) {
		this.openInterest = i;
	}

	@Override
	public double openPrice() {
		return this.openPrice;
	}

	@Override
	public void openPrice(double d) {
		this.openPrice = d;
	}

	@Override
	public String symbol() {
		return this.symbol;
	}

	@Override
	public void symbol(String s) {
		this.symbol = s;
	}

	@Override
	public LocalDate tradingDay() {
		return this.tradingDay;
	}

	@Override
	public void tradingDay(LocalDate d) {
		this.tradingDay = d;
	}

	@Override
	public Instant updateTime() {
		return this.updateTime;
	}

	@Override
	public void updateTime(Instant i) {
		this.updateTime = i;
	}

	@Override
	public int volume() {
		return this.volume;
	}

	@Override
	public void volume(int i) {
		this.volume = i;
	}

}
