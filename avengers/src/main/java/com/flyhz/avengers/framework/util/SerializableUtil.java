
package com.flyhz.avengers.framework.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SerializableUtil {
	private static final Logger	LOG	= LoggerFactory.getLogger(SerializableUtil.class);

	public static byte[] serializ(Object obj) throws IOException {
		if (obj != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(); // 构造一个字节输出流
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(baos);
				oos.writeObject(obj); // 写这个对象
				byte[] buf = baos.toByteArray(); // 从这个地层字节流中把传输的数组给一个新的数组
				oos.flush();
				return buf;
			} finally {
				if (oos != null) {
					try {
						oos.close();
					} catch (IOException e) {
						LOG.error("", e);
					}
				}
				if (baos != null) {
					try {
						baos.close();
					} catch (IOException e) {
						LOG.error("", e);
					}
				}

			}

		}
		return null;

	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] buf, Class<T> clazz) throws IOException,
			ClassNotFoundException {
		if (buf != null && buf.length > 0 && clazz != null) {
			ByteArrayInputStream bais = null;
			ObjectInputStream ois = null;
			try {
				bais = new ByteArrayInputStream(buf);
				ois = new ObjectInputStream(bais);
				return (T) ois.readObject();
			} finally {
				if (bais != null) {
					try {
						bais.close();
					} catch (IOException e) {
						LOG.error("", e);
					}
				}
				if (ois != null) {
					try {
						ois.close();
					} catch (IOException e) {
						LOG.error("", e);
					}
				}

			}

		}
		return null;

	}
}
