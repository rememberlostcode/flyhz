
package com.flyhz.avengers.framework.util;

import com.flyhz.avengers.framework.lang.HBaseAVTable;

public class IdFactory {
	private final long			id;
	private final static long	TWEPOCH				= 1361753741828L;
	private long				sequence			= 0L;
	private final static long	ID_BITS				= 4L;
	public final static long	MAX_ID				= -1L ^ -1L << ID_BITS;
	private final static long	SEQUENCE_BITS		= 10L;

	private final static long	ID_SHIFT			= SEQUENCE_BITS;
	private final static long	TIMESTAMP_LEFTSHIFT	= SEQUENCE_BITS + ID_BITS;
	public final static long	SEQUENCE_MASK		= -1L ^ -1L << SEQUENCE_BITS;

	private long				lastTimestamp		= -1L;

	public static IdFactory getInstance() {
		return Singletone.singletone;
	}

	static class Singletone {
		private static final IdFactory	singletone	= new IdFactory(2);	;

	}

	public IdFactory(final long id) {
		if (id > MAX_ID || id < 0) {
			throw new IllegalArgumentException(String.format(
					"worker Id can't be greater than %d or less than 0", MAX_ID));
		}
		this.id = id;
	}

	public synchronized long nextId() {
		long timestamp = this.timeGen();
		if (this.lastTimestamp == timestamp) {
			this.sequence = (this.sequence + 1) & SEQUENCE_MASK;
			if (this.sequence == 0) {
				timestamp = this.tilNextMillis(this.lastTimestamp);
			}
		} else {
			this.sequence = 0;
		}
		if (timestamp < this.lastTimestamp) {
			try {
				throw new Exception(String.format(
						"Clock moved backwards.  Refusing to generate id for %d milliseconds",
						this.lastTimestamp - timestamp));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		this.lastTimestamp = timestamp;
		long nextId = ((timestamp - TWEPOCH << TIMESTAMP_LEFTSHIFT)) | (this.id << ID_SHIFT)
				| (this.sequence);
		return nextId;
	}

	private long tilNextMillis(final long lastTimestamp) {
		long timestamp = this.timeGen();
		while (timestamp <= lastTimestamp) {
			timestamp = this.timeGen();
		}
		return timestamp;
	}

	private long timeGen() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		System.out.println(HBaseAVTable.av_fetch.name());
	}

}