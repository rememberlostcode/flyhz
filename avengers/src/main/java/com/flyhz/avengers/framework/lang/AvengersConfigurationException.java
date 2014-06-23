
package com.flyhz.avengers.framework.lang;

public class AvengersConfigurationException extends RuntimeException {

	public AvengersConfigurationException(String msg) {
		super(msg);
	}

	public AvengersConfigurationException() {
		super();

	}

	public AvengersConfigurationException(String arg0, Throwable arg1) {
		super(arg0, arg1);

	}

	public AvengersConfigurationException(Throwable arg0) {
		super(arg0);

	}

	private static final long	serialVersionUID	= -3043647139868542228L;

	public static void main(String[] args) {
		for (int i = 0; i < 100; i++) {
			if (i % 2 == 0) {
				continue;
			}
			System.out.println(i);
		}
	}
}
