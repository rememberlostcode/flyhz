
package com.flyhz.shop.dto;

import java.math.BigDecimal;
import java.util.List;

public class Order {

	private Integer			id;

	private List			orderDetails;

	private Character		status;

	private ConsigneeDetail	consigneeDetail;

	private BigDecimal		total;

	private List			vouchers;

	private User			user;

}