
package com.flyhz.shop.persistence.dao;

import com.flyhz.shop.persistence.entity.IdcardModel;

public interface IdcardDao extends GenericDao<IdcardModel> {

	public void insertIdcard(IdcardModel idcardModel);

	public IdcardModel getModelByName(IdcardModel idcardModel);

}
