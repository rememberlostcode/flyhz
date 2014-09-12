
package com.holding.smile.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.holding.smile.entity.Consignee;
import com.holding.smile.entity.UserDto;

/**
 * 
 * 类说明：订单信息
 * 
 * @author robin 2014-4-22下午3:23:51
 * 
 */
public class OrderDto implements Serializable {

	private static final long		serialVersionUID	= 4145258738736071421L;

	private Integer					id;

	private String					number;

	private List<OrderDetailDto>	details;

	private Consignee				consignee;

	private BigDecimal				logisticsPriceTotal;

	private BigDecimal				total;

	private Integer					qty;

	private List<VoucherDto>		vouchers;

	private UserDto					user;

	private String					time;

	private String					status;

	private String					tid;

	private LogisticsDto			logisticsDto;

	private RefundDto				refundDto;

	public RefundDto getRefundDto() {
		return refundDto;
	}

	public void setRefundDto(RefundDto refundDto) {
		this.refundDto = refundDto;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public List<OrderDetailDto> getDetails() {
		return details;
	}

	public void setDetails(List<OrderDetailDto> details) {
		this.details = details;
	}

	public Consignee getConsignee() {
		return consignee;
	}

	public void setConsignee(Consignee consignee) {
		this.consignee = consignee;
	}

	public BigDecimal getLogisticsPriceTotal() {
		return logisticsPriceTotal;
	}

	public void setLogisticsPriceTotal(BigDecimal logisticsPriceTotal) {
		this.logisticsPriceTotal = logisticsPriceTotal;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public List<VoucherDto> getVouchers() {
		return vouchers;
	}

	public void setVouchers(List<VoucherDto> vouchers) {
		this.vouchers = vouchers;
	}

	public UserDto getUser() {
		return user;
	}

	public void setUser(UserDto user) {
		this.user = user;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public Integer getQty() {
		return qty;
	}

	public void setQty(Integer qty) {
		this.qty = qty;
	}

	public LogisticsDto getLogisticsDto() {
		return logisticsDto;
	}

	public void setLogisticsDto(LogisticsDto logisticsDto) {
		this.logisticsDto = logisticsDto;
	}

	public String getTid() {
		return tid;
	}

	public void setTid(String tid) {
		this.tid = tid;
	}
}