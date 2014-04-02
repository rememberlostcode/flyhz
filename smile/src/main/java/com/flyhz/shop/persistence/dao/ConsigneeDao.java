
package com.flyhz.shop.persistence.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.flyhz.shop.dto.ConsigneeDto;
import com.flyhz.shop.persistence.entity.ConsigneeModel;

public interface ConsigneeDao extends GenericDao<ConsigneeModel> {

	public List<ConsigneeDto> getConsigneesByUserId(@Param(value = "id") Integer userId);

}
