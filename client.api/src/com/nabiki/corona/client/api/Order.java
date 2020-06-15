package com.nabiki.corona.client.api;

public interface Order {
	void symbol(String s);

	public void accountId(String s);

	void price(double d);

	double price();

	void stopPrice(double d);

	double stopPrice();

	void volume(int i);

	int volume();

	void minVolume(int i);

	int minVolume();

	void direction(char t);

	char direction();

	void priceType(char t);

	char priceType();

	void offsetFlag(char t);

	char offsetFlag();

	void hedgeFlag(char t);

	char hedgeFlag();

	void timeCondition(char t);

	char timeCondition();

	void volumeCondition(char t);

	char volumeCondition();

	void contigentConditon(char t);

	char contigentConditon();

	void note(String n);
}
