
package com.flyhz.shop.service;

import com.flyhz.shop.persistence.entity.IdcardModel;

public interface IdcardService {

	public void getAllIdcardsByUserId(Integer userId);

	public void saveIdcard(IdcardModel idcardModel);

	public void deleteIdcard(Integer id);
}
