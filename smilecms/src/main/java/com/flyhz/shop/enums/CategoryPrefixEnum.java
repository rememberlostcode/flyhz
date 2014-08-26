
package com.flyhz.shop.enums;

/**
 * 产品分类ENUM
 * 
 * @author fuwb
 */
public enum CategoryPrefixEnum {
	// 分类前缀
	HANDBAGS(1, "handbags"),
	WALLETS(2, "wallets"),
	ACCESSORIE(3, "accessorie"),
	JEWELRY(4, "jewelry"),
	MEN(5, "men"),
	CLEARANCE(6, "clearance"),
	NEWARRIVA(7, "newarriva"),
	CLOTHING(8, "clothing"),
	SHOES(9, "shoes");

	private Integer	categoryId;
	private String	categoryPrefix;

	private CategoryPrefixEnum(Integer categoryId, String categoryPrefix) {
		this.categoryId = categoryId;
		this.categoryPrefix = categoryPrefix;
	}

	public static String getCategoryPrefix(Integer categoryId) {
		for (CategoryPrefixEnum c : CategoryPrefixEnum.values()) {
			if (c.getCategoryId().equals(categoryId)) {
				return c.getCategoryPrefix();
			}
		}
		return null;
	}

	public Integer getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Integer categoryId) {
		this.categoryId = categoryId;
	}

	public String getCategoryPrefix() {
		return categoryPrefix;
	}

	public void setCategoryPrefix(String categoryPrefix) {
		this.categoryPrefix = categoryPrefix;
	}
}
