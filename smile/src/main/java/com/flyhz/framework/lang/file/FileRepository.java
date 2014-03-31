
package com.flyhz.framework.lang.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public interface FileRepository {

	public boolean checkFileExisit(String fileName);

	public void saveToTemp(InputStream in, String fileName) throws IOException;

	public boolean save(String fileName, Integer userId) throws IOException;

	public File copyFromTemp(String fileId, String fileType, String userId) throws IOException;

	public boolean copyFromTemp(String fileId, String fileType, String newName, Integer userId)
			throws IOException;

	public File getFile(String path);

	public boolean checkFileType(String fileType);
}
