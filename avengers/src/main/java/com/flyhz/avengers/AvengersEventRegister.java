
package com.flyhz.avengers;

import java.util.ArrayList;
import java.util.List;

public class AvengersEventRegister {

	private List<AvengersEvent>	events	= new ArrayList<AvengersEvent>();

	private static class Singleton {
		final static AvengersEventRegister	register	= new AvengersEventRegister();
	}

	public static AvengersEventRegister getInstance() {
		return Singleton.register;
	}

	private AvengersEventRegister() {

	}

	public void register(AvengersEvent event) {
		events.add(event);
	}
}
