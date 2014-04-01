
package com.flyhz.framework.controller;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.lang.multipart.FileUpload;
import com.flyhz.framework.lang.multipart.FileUploadStatus;
import com.flyhz.framework.lang.multipart.FileUploadValidateStatus;
import com.flyhz.framework.util.JSONUtil;

@Controller
public class FileController {

	protected Logger		log				= LoggerFactory.getLogger(FileController.class);

	@Resource
	private FileRepository	_fileRepository;

	private Properties		contentTypes	= new Properties();

	public FileController() throws IOException {
		contentTypes.load(this.getClass().getClassLoader()
								.getResourceAsStream("contentType.properties"));
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public String doFileUpload(HttpServletRequest request) {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> multipartFiles = multipartRequest.getMultiFileMap()
																	.toSingleValueMap();
		for (MultipartFile file : multipartFiles.values()) {
			FileUploadStatus status = FileUpload.getStatus(file.getName());
			if (status != null) {
				try {
					_fileRepository.saveToTemp(file.getInputStream(), file.getName());
					status.setComplete(true);
					return JSONUtil.mapper.writeValueAsString(status);
				} catch (IOException e) {
					log.error("", e);
					status.setError(true);
					try {
						return JSONUtil.mapper.writeValueAsString(status);
					} catch (Exception e1) {
						log.error("", e1);
					}
				}
			}
		}
		return "";
	}

	@RequestMapping(value = "/upload/rid")
	@ResponseBody
	public String prepareFileUpload(HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		return UUID.randomUUID().toString();
	}

	@ExceptionHandler(MultipartException.class)
	@ResponseBody
	public String handleException(MultipartException ex) {
		try {
			if (ex instanceof MaxUploadSizeExceededException) {
				return JSONUtil.mapper.writeValueAsString(new FileUploadValidateStatus(new String(
						"超出文件上传限制大小".getBytes(), "ISO-8859-1"), null));
			}
			log.error("", ex);
			return JSONUtil.mapper.writeValueAsString(new FileUploadValidateStatus(new String(
					"出错了".getBytes(), "ISO-8859-1"), null));
		} catch (Exception e) {
		}
		return "";

	}

	@RequestMapping(value = "/upload/{id}/status", method = RequestMethod.GET)
	@ResponseBody
	public String doFileUploadStatus(@PathVariable("id") String id, WebRequest webRequest)
			throws IOException {
		FileUploadStatus status = FileUpload.getStatus(id);
		if (status != null) {
			return JSONUtil.mapper.writeValueAsString(status);
		}
		return "123";
	}

	@RequestMapping(value = "/upload/{id}/cancel", method = RequestMethod.GET)
	@ResponseBody
	public String doFileUploadCancel(@PathVariable("id") String id, WebRequest webRequest)
			throws IOException {
		FileUploadStatus status = FileUpload.getStatus(id);
		if (status != null) {
			status.setCancel(true);
			return "success";
		}
		return "";
	}

}
