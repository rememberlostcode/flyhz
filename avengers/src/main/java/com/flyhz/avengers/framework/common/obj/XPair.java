
package com.flyhz.avengers.framework.common.obj;

public class XPair<T, E> {

	T	first;
	E	second;

	public XPair(T start, E end) {
		super();
		this.first = start;
		this.second = end;
	}

	public T getFirst() {
		return first;
	}

	public E getSecond() {
		return second;
	}
}
