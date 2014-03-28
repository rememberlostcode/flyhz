
package com.flyhz.framework.view.multipart;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.flyhz.framework.repository.file.FileRepository;

public class LostMultipartResolver extends CommonsMultipartResolver {

	@Resource
	private FileRepository	fileRepository;

	public LostMultipartResolver() {
		super();
	}

	public LostMultipartResolver(ServletContext servletContext) {
		super(servletContext);
	}

	@Override
	protected org.apache.commons.fileupload.FileUpload newFileUpload(FileItemFactory fileItemFactory) {
		return new FileUpload(getFileItemFactory(), fileRepository);
	}

	@Override
	protected FileUpload prepareFileUpload(String encoding) {
		FileUpload fileUpload = (FileUpload) getFileUpload();
		FileUpload actualFileUpload = fileUpload;

		// Use new temporary FileUpload instance if the request specifies
		// its own encoding that does not match the default encoding.
		if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
			actualFileUpload = (FileUpload) newFileUpload(getFileItemFactory());
			actualFileUpload.setSizeMax(fileUpload.getSizeMax());
			actualFileUpload.setHeaderEncoding(encoding);
		}

		return actualFileUpload;
	}

	@Override
	protected MultipartParsingResult parseRequest(HttpServletRequest request)
			throws MultipartException {
		List<FileUploadStatus> list = new ArrayList<FileUploadStatus>();
		request.setAttribute("status", list);
		String encoding = determineEncoding(request);
		FileUpload fileUpload = prepareFileUpload(encoding);
		try {
			@SuppressWarnings("unchecked")
			List<FileItem> fileItems = fileUpload.parseRequest(request);
			return parseFileItems(fileItems, encoding);
		} catch (FileUploadBase.SizeLimitExceededException ex) {
			throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), ex);
		} catch (FileUploadException ex) {
			throw new MultipartException("Could not parse multipart servlet request", ex);
		}
	}

	@Override
	public void cleanupMultipart(MultipartHttpServletRequest request) {
	}
}
