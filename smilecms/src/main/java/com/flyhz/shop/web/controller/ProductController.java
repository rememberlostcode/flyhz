
package com.flyhz.shop.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.framework.lang.page.Pagination;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.shop.dto.ProductCmsDto;
import com.flyhz.shop.dto.ProductParamDto;
import com.flyhz.shop.dto.ProductShowDto;
import com.flyhz.shop.persistence.entity.ProductModel;
import com.flyhz.shop.service.BrandService;
import com.flyhz.shop.service.CategoryService;
import com.flyhz.shop.service.ProductService;

/**
 * 产品Controller
 * 
 * @author fuwb
 * 
 */
@Controller
@RequestMapping(value = "/product")
public class ProductController {
	@Resource
	private ProductService	productService;
	@Resource
	private BrandService	brandService;
	@Resource
	private CategoryService	categoryService;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String listProducts(@Pagination Pager pager, Model model) {
		pager.setPageSize(15);
		List<ProductCmsDto> productCmsDtos = productService.getProductCmsDtosByPage(pager);
		model.addAttribute("products", productCmsDtos);
		model.addAttribute("page", pager);
		model.addAttribute("current", "1");
		return "/product/list";
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteProduct(@RequestParam(value = "productId") Integer productId, Model model) {
		try {
			productService.deleteProduct(productId);
			model.addAttribute("message", "操作成功");
		} catch (ValidateException e) {
			model.addAttribute("message", e.getMessage());
		}
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/product/list";
	}

	@RequestMapping(value = "/batchdel", method = RequestMethod.POST)
	public String deleteProducts(@RequestParam(value = "productIds") String productIds, Model model) {
		try {
			productService.batchDelProducts(productIds);
			model.addAttribute("message", "操作成功");
		} catch (ValidateException e) {
			model.addAttribute("message", e.getMessage());
		}
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/product/list";
	}

	@RequestMapping(value = "/show", method = RequestMethod.GET)
	public String showProduct(@RequestParam(value = "productId") Integer productId, Model model) {
		ProductModel productModel = productService.getProductById(productId);
		ProductShowDto productShowDto = new ProductShowDto();
		BeanUtils.copyProperties(productModel, productShowDto);
		// 初始化产品coverImgs
		if (StringUtils.isNotBlank(productShowDto.getCover())) {
			List<String> productImgs = JSONUtil.getJson2EntityList(productShowDto.getCover(),
					ArrayList.class, String.class);
			productShowDto.setProductImgs(productImgs);
			productShowDto.setProductImgsCount(productImgs.size() + 1);
		}
		// 初始化产品srcImgs
		if (StringUtils.isNotBlank(productModel.getImgs())) {
			List<String> productSrcImgs = JSONUtil.getJson2EntityList(productShowDto.getImgs(),
					ArrayList.class, String.class);
			productShowDto.setProductSrcImgs(productSrcImgs);
		}
		model.addAttribute("product", productShowDto);
		// 查询品牌列表
		model.addAttribute("brands", brandService.getAllBrandBuildDtos());
		// 查询分类列表
		model.addAttribute("cates", categoryService.getAllCategoryBuildDtos());
		return "/product/show";
	}

	@RequestMapping(value = "/addpage", method = RequestMethod.GET)
	public String addpage(Model model) {
		// 查询品牌列表
		model.addAttribute("brands", brandService.getAllBrandBuildDtos());
		// 查询分类列表
		model.addAttribute("cates", categoryService.getAllCategoryBuildDtos());
		return "/product/addpage";
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public String add(ProductParamDto product, HttpServletRequest request, Model model) {
		try {
			productService.addProduct(product);
			model.addAttribute("message", "操作成功");
		} catch (Exception e) {
			model.addAttribute("message", e.getMessage());
		}
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/product/list";
	}

	@RequestMapping(value = "/edit", method = RequestMethod.POST)
	public String editProduct(ProductParamDto product, Model model) {
		try {
			productService.editProduct(product);
			model.addAttribute("message", "操作成功");
		} catch (ValidateException e) {
			model.addAttribute("message", e.getMessage());
		}
		return UrlBasedViewResolver.REDIRECT_URL_PREFIX + "/product/list";
	}
}
