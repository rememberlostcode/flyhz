
package com.flyhz.framework.auth;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class IdentifyArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(Identify.class);
	}

	@Resource(name = "auth")
	private Authenticate	auth;

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		if (Integer.class.equals(parameter.getParameterType())) {
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
			Integer id = WebUser.currentWebUser();
			if (id == null) {
				return auth.recognize(request, response);
			}
			return id;
		}
		return null;
	}
}
