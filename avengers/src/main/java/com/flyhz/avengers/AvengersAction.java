
package com.flyhz.avengers;

public interface AvengersAction<E> {

	public void action(AvengersEvent<E> ae);
}
