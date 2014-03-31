
package com.flyhz.framework.lang.file;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.framework.util.StringUtil;

public class FileRepositoryImpl implements FileRepository {

	protected final Logger		log	= LoggerFactory.getLogger(this.getClass());

	private String				pathFileUpload;

	private String				pathFileTemp;

	private Collection<String>	fileTypesAllow;

	public FileRepositoryImpl(String fileTypesAllow, String pathFileUpload, String pathFileTemp) {
		if (StringUtil.isEmpty(fileTypesAllow)) {
			this.fileTypesAllow = new HashSet<String>();
		}
		this.fileTypesAllow = StringUtil.convertStringToStringCollection(fileTypesAllow.toLowerCase());
		this.pathFileTemp = pathFileTemp;
		this.pathFileUpload = pathFileUpload;
	}

	@Override
	public boolean checkFileExisit(String fileName) {
		return new File(pathFileTemp + File.separator + fileName).exists();
	}

	protected File getTempFile(String fileName) throws IOException {
		File file = new File(pathFileTemp + File.separator + fileName);
		if (file.exists()) {
			throw new IOException("file is exist,check the fileName " + fileName);
		}
		return file;
	}

	@Override
	public void saveToTemp(InputStream in, String fileName) throws IOException {
		File file = getTempFile(fileName);
		try {
			FileUtil.copy(in, new FileOutputStream(file));
		} catch (IOException e) {
			file.delete();
			throw e;
		}
	}

	@Override
	public boolean save(String fileName, Integer userId) {
		return false;
	}

	@Override
	public File copyFromTemp(String fileId, String fileType, String userId) throws IOException {
		if (userId == null) {
			String errorMsg = "userId can not be null";
			log.error(errorMsg);
			throw new IOException(errorMsg);
		}

		StringBuffer sb = new StringBuffer(30);
		sb.append(
				pathFileUpload.endsWith(File.separator) ? pathFileUpload
						: (pathFileUpload + File.separator)).append(userId).append(File.separator)
			.append(fileType).append(File.separator)
			.append(new SimpleDateFormat("yyyy-MM-dd").format(new Date())).append(File.separator);
		String dirPath = sb.toString();
		sb.append(fileId);
		String path = sb.toString();

		File oldFile = new File(pathFileTemp + File.separator + fileId);
		if (!oldFile.exists()) {
			throw new IOException("temp file not exists!");
		}
		File newFile = new File(path);
		if (newFile.exists()) {
			throw new IOException("file exists!");
		}
		File dir = new File(dirPath);
		dir.mkdirs();
		if (oldFile.renameTo(newFile)) {
			return newFile;
		}
		return null;
	}

	@Override
	public boolean copyFromTemp(String fileName, String fileType, String newName, Integer userId) {
		return false;
	}

	@Override
	public boolean checkFileType(String fileType) {
		if (StringUtil.isEmpty(fileType)) {
			return false;
		}
		return getFileTypesAllow().contains(fileType.toLowerCase());
	}

	public static void main(String[] args) {
		Collection<String> co = new HashSet<String>();
		co.add("txt");
		co.add("png");
		System.out.println(co.contains("txt"));

		File file = new File("/Users/huoding/oklfiletemp/test/2/2012/1.xml");
		file.mkdir();
	}

	@Override
	public File getFile(String path) {
		return new File(path);
	}

	public Collection<String> getFileTypesAllow() {
		return fileTypesAllow;
	}
}
