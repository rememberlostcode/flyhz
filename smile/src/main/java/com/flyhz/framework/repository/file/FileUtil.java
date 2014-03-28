
package com.flyhz.framework.repository.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.util.FileCopyUtils;


public class FileUtil extends FileCopyUtils {

	/**
	 * 
	 * @param in
	 * @param path
	 * @param filename
	 * @param size
	 * @param saveFileProcess
	 * @throws IOException
	 */

	public static int	BUFFER_SIZE	= 4096;

	public static void copy(InputStream in, OutputStream out, FileCopyStatus status)
			throws IOException, FileUploadCancelException {
		try {
			int bytesRead = 0;
			byte[] buffer = new byte[BUFFER_SIZE];
			int bytes = -1;
			if (status == null) {
				while ((bytes = in.read(buffer)) != -1) {
					out.write(buffer, 0, bytes);
					bytesRead += bytes;
				}
			} else {
				while ((bytes = in.read(buffer)) != -1) {
					if (status.isCancel()) {
						throw new FileUploadCancelException("cancel!");
					}
					out.write(buffer, 0, bytes);
					bytesRead += bytes;
					status.update(bytesRead);
				}
			}
			out.flush();
		} finally {
			try {
				in.close();
			} catch (IOException ex) {
			}
			try {
				out.close();
			} catch (IOException ex) {
			}
		}

	}

	public static void copy(InputStream in, File file, FileCopyStatus status) throws IOException,
			FileUploadCancelException {
		OutputStream out = new FileOutputStream(file);
		copy(in, out, status);

	}
}
