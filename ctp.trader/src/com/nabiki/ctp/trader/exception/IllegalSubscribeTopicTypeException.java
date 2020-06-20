package com.nabiki.ctp.trader.exception;

public class IllegalSubscribeTopicTypeException extends Exception {
	private static final long serialVersionUID = -8508618989048034176L;

	public IllegalSubscribeTopicTypeException() {
		super();
	}

	public IllegalSubscribeTopicTypeException(String message) {
		super(message);
	}
}
