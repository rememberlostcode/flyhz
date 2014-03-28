
package com.flyhz.framework.id;

import java.io.Serializable;

public class ID implements Serializable {

	/**
	 * 
	 */
	private static final long	serialVersionUID	= 5337003183441016456L;
	private String				username;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ID other = (ID) obj;
		if (username == null) {
			if (other.username != null)
				return false;
		} else if (!username.equals(other.username))
			return false;
		return true;
	}
}
