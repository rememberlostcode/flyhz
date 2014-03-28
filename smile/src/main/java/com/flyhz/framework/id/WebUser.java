
package com.flyhz.framework.id;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.flyhz.framework.lang.Authenticate;

@JsonIgnoreProperties(value = { "auth", "webUsers" })
public class WebUser {
	private ID											id;
	private transient static final ThreadLocal<WebUser>	webUsers	= new ThreadLocal<WebUser>();
	private transient String							displayName;
	private transient Authenticate						auth;

	public WebUser(ID id, Authenticate auth) {
		if (id == null) {
			throw new RuntimeException(
					"Constructor WebUser(ID id,Authenticate auth) id can't be null");
		}
		this.id = id;
		this.auth = auth;
		webUsers.set(this);
	}

	public static WebUser currentWebUser() {
		return webUsers.get();
	}

	public static void removeCurrentWebUser() {
		webUsers.remove();
	}

	public String getDisplayName() {
		if (this.displayName == null) {
			displayName = this.auth.display(this.id);
		}
		return displayName;
	}

	public void setAuth(Authenticate auth) {
		this.auth = auth;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public ID getId() {
		return id;
	}

	public String getUsername() {
		return id.getUsername();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((displayName == null) ? 0 : displayName.hashCode());
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
		if (displayName == null) {
			if (other.displayName != null)
				return false;
		} else if (!displayName.equals(other.displayName))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
