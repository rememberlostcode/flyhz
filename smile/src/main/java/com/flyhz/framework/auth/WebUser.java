
package com.flyhz.framework.auth;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(value = { "auth", "webUsers" })
public class WebUser {
	private Integer										id;
	private transient static final ThreadLocal<Integer>	webUsers	= new ThreadLocal<Integer>();

	public WebUser(Integer id) {
		if (id == null) {
			throw new RuntimeException(
					"Constructor WebUser(ID id,Authenticate auth) id can't be null");
		}
	}

	public static Integer currentWebUser() {
		return webUsers.get();
	}

	public static void removeCurrentWebUser() {
		webUsers.remove();
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WebUser other = (WebUser) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
