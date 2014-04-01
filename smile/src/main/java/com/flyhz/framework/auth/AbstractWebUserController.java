
package com.flyhz.framework.auth;

import org.springframework.web.bind.annotation.ModelAttribute;

public abstract class AbstractWebUserController {

	@ModelAttribute("webUser")
	public WebUser getWebUser(@Identify WebUser webUser) {
		return webUser;
	}
}
