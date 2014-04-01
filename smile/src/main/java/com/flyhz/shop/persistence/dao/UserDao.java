
package com.flyhz.shop.persistence.dao;

import com.flyhz.framework.lang.BusinessException;
import com.flyhz.shop.dto.UserDetail;

public interface UserDao extends GenericDao<UserDetail> {

	public void register(UserDetail userDetail) throws BusinessException;

}
