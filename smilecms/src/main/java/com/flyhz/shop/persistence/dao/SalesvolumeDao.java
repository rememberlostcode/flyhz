
package com.flyhz.shop.persistence.dao;

import java.util.Date;

import com.flyhz.shop.persistence.entity.SalesvolumeModel;

public interface SalesvolumeDao extends GenericDao<SalesvolumeModel> {

	public Date getLastStartDate();

	public Date getLastEndDate();
}
