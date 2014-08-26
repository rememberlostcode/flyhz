
package com.flyhz.shop.enums;


/**
 * 品牌前缀ENUM
 * 
 * @author fuwb
 */
public enum BrandPrefixEnum {
	// 品牌前缀
	CHANEL(1, "chanel"),
	COACH(2, "coach"),
	PRADA(3, "prada"),
	SWAROVSKI(4, "swarovski"),
	ABERCROMBIE(5, "abercrombie"),
	CALVINKLEIN(6, "calvinklein"),
	COLUMBIA(7, "columbia"),
	DKNY(8, "dkny"),
	KATESPADE(9, "katespade"),
	LACOSTE(10, "lacoste"),
	MICHAELKORS(11, "michaelkors"),
	NAUTICA(12, "nautica"),
	THENORTHFACE(13, "thenorthface"),
	RALPHLAUREN(14, "ralphlauren"),
	TOMMY(15, "tommy"),
	FOSSIL(16, "fossil"),
	KIPLING(17, "kipling"),
	SUNGLASSHUT(18, "sunglasshut"),
	ESTEELAUDER(19, "esteelauder"),
	CLINIQUE(20, "clinique"),
	KIEHLS(21, "kiehls"),
	ORIGINS(22, "origins"),
	PETERTHOMASROTH(23, "peterthomasroth"),
	FRESH(24, "fresh"),
	BENEFITCOSMETICS(25, "benefitcosmetics"),
	ANNASUI(26, "annasui"),
	NARSCOSMETICS(27, "narscosmetics"),
	BOBBIBROWNCOSMETICS(28, "bobbibrowncosmetics"),
	MACCOSMETICS(29, "maccosmetics"),
	LAMER(30, "lamer"),
	COPPERTONE(31, "coppertone"),
	GLAMGLOW(32, "glamglow");

	private Integer	brandId;
	private String	brandPrefix;

	private BrandPrefixEnum(Integer brandId, String brandPrefix) {
		this.brandId = brandId;
		this.brandPrefix = brandPrefix;
	}

	public static String getBrandPrefix(Integer brandId) {
		for (BrandPrefixEnum b : BrandPrefixEnum.values()) {
			if (b.getBrandId().equals(brandId)) {
				return b.getBrandPrefix();
			}
		}
		return null;
	}

	public Integer getBrandId() {
		return brandId;
	}

	public void setBrandId(Integer brandId) {
		this.brandId = brandId;
	}

	public String getBrandPrefix() {
		return brandPrefix;
	}

	public void setBrandPrefix(String brandPrefix) {
		this.brandPrefix = brandPrefix;
	}
}
