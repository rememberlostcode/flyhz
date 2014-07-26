
package com.flyhz.avengers.framework.common.dto;

public class XPairDto<T, E> {

	T	first;
	E	second;

	public XPairDto(T start, E end) {
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
