
package com.flyhz.framework.id;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.flyhz.framework.lang.Authenticate;

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
		if (WebUser.class.equals(getSuperClass(parameter.getParameterType()))) {
			HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
			HttpServletResponse response = webRequest.getNativeResponse(HttpServletResponse.class);
			WebUser webUser = WebUser.currentWebUser();
			if (webUser == null) {
				return auth.identify(request, response);
			}
			return webUser;
		}
		return null;
	}

	protected Class<?> getSuperClass(Class<?> clazz) {
		if (clazz.getSuperclass().equals(Object.class)) {
			return clazz;
		} else {
			return getSuperClass(clazz.getSuperclass());
		}
	}

	public static void main(String[] args) {
		System.out.println(IdentifyArgumentResolver.class.getSuperclass());
		System.out.println(ID.class.equals(WebUser.class.getSuperclass()));
	}
}
