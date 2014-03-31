
package com.flyhz.framework.view.multipart;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileItemHeaders;
import org.apache.commons.fileupload.FileItemHeadersSupport;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.RequestContext;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.lang.file.FileUploadCancelException;
import com.flyhz.framework.lang.file.FileUtil;

public class FileUpload extends ServletFileUpload implements Job {

	protected Logger							log							= LoggerFactory.getLogger(getClass());

	static final Map<String, FileUploadStatus>	STATUS_MAP					= new ConcurrentHashMap<String, FileUploadStatus>();

	static final Map<String, Date>				STATUS_GENERATE_DATE_MAP	= new ConcurrentHashMap<String, Date>();

	private FileRepository						_fileRepository;

	public FileUpload() {

	}

	public FileUpload(FileItemFactory fileItemFactory, FileRepository fileRepository) {
		super(fileItemFactory);
		this._fileRepository = fileRepository;
	}

	public static FileUploadStatus getStatus(String key) {
		return STATUS_MAP.get(key);
	}

	static void putStatus(String key, FileUploadStatus status) {
		STATUS_MAP.put(key, status);
		STATUS_GENERATE_DATE_MAP.put(key, new Date());
	}

	@Override
	public List<FileItem> parseRequest(RequestContext ctx) throws FileUploadException {
		List<FileItem> items = new ArrayList<FileItem>();
		boolean successful = false;
		try {
			FileItemIterator iter = getItemIterator(ctx);
			FileItemFactory fac = getFileItemFactory();
			if (fac == null) {
				throw new NullPointerException("No FileItemFactory has been set.");
			}
			while (iter.hasNext()) {
				final FileItemStream item = iter.next();
				// Don't use getName() here to prevent an
				// InvalidFileNameException.
				final String fileName = item.getName();
				FileItem fileItem = fac.createItem(item.getFieldName(), item.getContentType(),
						item.isFormField(), fileName);
				items.add(fileItem);
				if (!fileItem.isFormField()) {
					if (_fileRepository.checkFileExisit(item.getFieldName())) {
						throw new IOException("file is exist,check the field name "
								+ item.getFieldName());
					}
					FileUploadStatus status = getStatus(item.getFieldName());
					if (status == null) {
						status = new FileUploadStatus(item.getFieldName(), ctx.getContentLength());
						putStatus(item.getFieldName(), status);
					}
					try {
						FileUtil.copy(item.openStream(), fileItem.getOutputStream(), status);
					} catch (FileUploadCancelException e) {
						log.info("user cancel upload input name = {}, file name = {}",
								item.getFieldName(), fileName);
					} catch (IOException e) {
						log.error("file upload error input name = {},fileName = {}",
								item.getFieldName(), fileName);
						status.setError(true);
						throw e;
					}
				} else {
					try {
						Streams.copy(item.openStream(), fileItem.getOutputStream(), true);
					} catch (FileUploadIOException e) {
						throw (FileUploadException) e.getCause();
					} catch (IOException e) {
						throw new IOFileUploadException("Processing of " + MULTIPART_FORM_DATA
								+ " request failed. " + e.getMessage(), e);
					}
				}
				if (fileItem instanceof FileItemHeadersSupport) {
					final FileItemHeaders fih = item.getHeaders();
					((FileItemHeadersSupport) fileItem).setHeaders(fih);
				}

			}
			successful = true;
			return items;
		} catch (FileUploadIOException e) {
			throw (FileUploadException) e.getCause();
		} catch (IOException e) {
			throw new FileUploadException(e.getMessage(), e);
		} finally {
			if (!successful) {
				for (Iterator<FileItem> iterator = items.iterator(); iterator.hasNext();) {
					FileItem fileItem = (FileItem) iterator.next();
					try {
						fileItem.delete();
					} catch (Throwable e) {
						// ignore it
					}
				}
			}
		}
	}

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
		Date outDate = calendar.getTime();

		for (Iterator<Entry<String, Date>> it = STATUS_GENERATE_DATE_MAP.entrySet().iterator(); it.hasNext();) {
			Entry<String, Date> entry = it.next();
			Date date = entry.getValue();
			if (date.before(outDate)) {
				it.remove();
			}
		}
	}

}
