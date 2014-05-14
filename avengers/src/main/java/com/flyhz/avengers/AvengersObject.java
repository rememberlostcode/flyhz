
package com.flyhz.avengers;

import java.util.ArrayList;
import java.util.List;

public abstract class AvengersObject<E> {

	protected List<AvengersAction<E>>	actions	= new ArrayList<AvengersAction<E>>();

	public void addAction(AvengersAction<E> action) {
		actions.add(action);
	};
}
