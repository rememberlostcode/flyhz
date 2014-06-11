
package com.flyhz.shop.service.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.lost.image.ImageUtil;
import org.springframework.web.multipart.MultipartFile;

import com.flyhz.framework.lang.SolrData;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.lang.file.FileRepository;
import com.flyhz.framework.lang.page.Pager;
import com.flyhz.framework.util.JSONUtil;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductCmsDto;
import com.flyhz.shop.dto.ProductParamDto;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.ProductModel;
import com.flyhz.shop.service.ProductService;

public class ProductServiceImpl implements ProductService {
	@Resource
	private ProductDao					productDao;
	@Resource
	private FileRepository				fileRepository;
	@Resource
	private SolrData					solrData;

	private String						pathFileUpload;

	// 初始化保存图片的前缀
	private static Map<String, String>	prefixMap	= new HashMap<String, String>();
	static {
		prefixMap.put("1", "chanel");
		prefixMap.put("2", "coach");
		prefixMap.put("3", "prada");
		prefixMap.put("4", "swarovski");
		prefixMap.put("99", "color");
	}

	public String getPathFileUpload() {
		return pathFileUpload;
	}

	public void setPathFileUpload(String pathFileUpload) {
		this.pathFileUpload = pathFileUpload;
	}

	@Override
	public List<ProductModel> getProductsByPage(Pager pager) {
		List<ProductModel> products = productDao.getPagedProducts(pager);
		return products;
	}

	@Override
	public void addProduct(ProductParamDto productParamDto) throws ValidateException {
		if (productParamDto == null || StringUtils.isBlank(productParamDto.getName())) {
			throw new ValidateException("产品名称为空");
		}
		if (StringUtil.stringLength(productParamDto.getName()) > 128) {
			throw new ValidateException("产品名称过长");
		}
		if (productParamDto.getBrandId() == null) {
			throw new ValidateException("产品品牌为空");
		}
		if (productParamDto.getCategoryId() == null) {
			throw new ValidateException("产品分类为空");
		}
		if (StringUtils.isBlank(productParamDto.getBrandstyle())) {
			throw new ValidateException("产品款号为空");
		}
		if (StringUtil.stringLength(productParamDto.getBrandstyle()) > 16) {
			throw new ValidateException("产品款号过长");
		}
		if (StringUtil.stringLength(productParamDto.getDescription()) > 512) {
			throw new ValidateException("产品描述过长");
		}
		if (StringUtil.stringLength(productParamDto.getColor()) > 32) {
			throw new ValidateException("产品颜色过长");
		}
		// 生成产品Model对象
		ProductModel productModel = new ProductModel();
		productModel.setName(productParamDto.getName());
		productModel.setBrandId(productParamDto.getBrandId());
		productModel.setBrandstyle(productParamDto.getBrandstyle());
		productModel.setCategoryId(productParamDto.getCategoryId());
		productModel.setColor(productParamDto.getColor());
		if (StringUtils.isNotBlank(productParamDto.getDescription())) {
			productModel.setDescription(productParamDto.getDescription());
		} else {
			productModel.setDescription(productModel.getName());
		}
		productModel.setGmtCreate(new Date());
		productModel.setGmtModify(new Date());
		productModel.setLocalprice(productParamDto.getLocalprice());
		productModel.setPurchasingprice(productParamDto.getPurchasingprice());
		int maxStyle = productDao.getMaxStyle();
		productModel.setStyle(String.valueOf(maxStyle + 1));
		// 处理颜色图片
		dispostColorimg(productParamDto.getColorimg(), productModel, productParamDto);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel);
		productDao.addProduct(productModel);
		// builder数据到Solr中
		ProductBuildDto productBuildDto = productDao.getProductBuildDtoById(productModel.getId());
		solrData.submitProduct(productBuildDto);
	}

	@Override
	public void deleteProduct(Integer productId) throws ValidateException {
		if (productId == null) {
			throw new ValidateException("产品不存在!");
		}
		productDao.deleteProduct(productId);
	}

	@Override
	public void batchDelProducts(String productIds) throws ValidateException {
		if (StringUtils.isBlank(productIds)) {
			throw new ValidateException("产品不存在!");
		}
		String[] productIdsArray = productIds.split(",");
		for (int i = 0; i < productIdsArray.length; i++) {
			Integer productId = Integer.parseInt(productIdsArray[i]);
			if (productId != null) {
				productDao.deleteProduct(productId);
			}
		}
	}

	@Override
	public void editProduct(ProductParamDto productParamDto) throws ValidateException {
		if (productParamDto == null || productParamDto.getId() == null) {
			throw new ValidateException("产品不存在");
		}
		ProductModel productModel = productDao.getModelById(productParamDto.getId());
		if (productModel == null) {
			throw new ValidateException("产品不存在");
		}
		if (StringUtils.isBlank(productParamDto.getName())) {
			throw new ValidateException("产品名称为空");
		}
		if (StringUtil.stringLength(productParamDto.getName()) > 128) {
			throw new ValidateException("产品名称过长");
		}
		if (productParamDto.getBrandId() == null) {
			throw new ValidateException("产品品牌为空");
		}
		if (productParamDto.getCategoryId() == null) {
			throw new ValidateException("产品分类为空");
		}
		if (StringUtils.isBlank(productParamDto.getBrandstyle())) {
			throw new ValidateException("产品款号为空");
		}
		if (StringUtil.stringLength(productParamDto.getBrandstyle()) > 16) {
			throw new ValidateException("产品款号过长");
		}
		if (StringUtil.stringLength(productParamDto.getDescription()) > 512) {
			throw new ValidateException("产品描述过长");
		}
		if (StringUtil.stringLength(productParamDto.getColor()) > 32) {
			throw new ValidateException("产品颜色过长");
		}
		// 生成产品Model对象
		productModel.setName(productParamDto.getName());
		productModel.setBrandId(productParamDto.getBrandId());
		productModel.setBrandstyle(productParamDto.getBrandstyle());
		productModel.setCategoryId(productParamDto.getCategoryId());
		productModel.setColor(productParamDto.getColor());
		if (StringUtils.isNotBlank(productParamDto.getDescription())) {
			productModel.setDescription(productParamDto.getDescription());
		} else {
			productModel.setDescription(productModel.getName());
		}
		productModel.setGmtModify(new Date());
		productModel.setLocalprice(productParamDto.getLocalprice());
		productModel.setPurchasingprice(productParamDto.getPurchasingprice());
		// 处理颜色图片
		dispostColorimg(productParamDto.getColorimg(), productModel, productParamDto);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel, productParamDto);
		productDao.editProduct(productModel);
		// builder数据到Solr中
		ProductBuildDto productBuildDto = productDao.getProductBuildDtoById(productModel.getId());
		solrData.submitProduct(productBuildDto);
	}

	@Override
	public ProductModel getProductById(Integer productId) {
		if (productId != null) {
			return productDao.getModelById(productId);
		}
		return null;
	}

	@Override
	public List<ProductCmsDto> getProductCmsDtosByPage(Pager pager) {
		List<ProductCmsDto> productCmsDtos = productDao.getPageProductCmsDtos(pager);
		return productCmsDtos;
	}

	private void dispostColorimg(MultipartFile file, ProductModel productModel,
			ProductParamDto productParamDto) {
		if (file != null && productModel != null && productParamDto != null
				&& StringUtils.isBlank(productParamDto.getOldColorimg())) {
			String origName = file.getOriginalFilename();
			if (StringUtils.isNotBlank(origName)) {
				StringBuffer newFileName = new StringBuffer();
				newFileName.append(prefixMap.get("99")).append(File.separatorChar)
							.append(productModel.getStyle())
							.append(origName.substring(origName.lastIndexOf(".")));
				// 保存颜色图到指定文件路径里
				try {
					String filename = fileRepository.saveToTarget(file.getInputStream(),
							newFileName.toString());
					productModel.setColorimg(File.separatorChar + filename);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void dispostProductImgs(List<MultipartFile> files, ProductModel productModel) {
		if (files != null && !files.isEmpty() && productModel != null) {
			List<String> bigImgUrls = new ArrayList<String>();
			List<String> litImgUrls = new ArrayList<String>();
			try {
				int count = 0;
				for (MultipartFile multipartFile : files) {
					String origName = multipartFile.getOriginalFilename();
					if (StringUtils.isNotBlank(origName)) {
						StringBuffer newFileName = new StringBuffer();
						newFileName.append(prefixMap.get(String.valueOf(productModel.getBrandId())))
									.append(File.separatorChar).append(productModel.getStyle())
									.append("_").append(count)
									.append(origName.substring(origName.lastIndexOf(".")));
						// 处理物品大图
						String bigPath = fileRepository.saveToTarget(
								multipartFile.getInputStream(), newFileName.toString());
						if (StringUtils.isNotBlank(bigPath)) {
							bigImgUrls.add(File.separatorChar + bigPath);
						}
						// 处理物品小图
						String litPath = ImageUtil.createSquareImage(pathFileUpload
								+ File.separatorChar + bigPath, 300, "lit");
						if (StringUtils.isNotBlank(litPath)) {
							litImgUrls.add(File.separatorChar
									+ prefixMap.get(String.valueOf(productModel.getBrandId()))
									+ File.separatorChar + litPath.replace("_lit", ""));
						}
					}
					count++;
				}
				productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
				List<String> coverSmall = new ArrayList<String>();
				coverSmall.add(litImgUrls.get(0));
				productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void dispostProductImgs(List<MultipartFile> files, ProductModel productModel,
			ProductParamDto productParamDto) {
		if (productModel != null && productParamDto != null) {
			List<String> bigImgUrls = productParamDto.getProductSrcImgs() == null ? new ArrayList<String>()
					: productParamDto.getProductSrcImgs();
			List<String> litImgUrls = productParamDto.getProductImgs() == null ? new ArrayList<String>()
					: productParamDto.getProductImgs();
			try {
				if (files != null && !files.isEmpty()) {
					int count = 0;
					for (MultipartFile multipartFile : files) {
						String origName = multipartFile.getOriginalFilename();
						if (StringUtils.isNotBlank(origName)) {
							StringBuffer newFileName = new StringBuffer();
							newFileName.append(
									prefixMap.get(String.valueOf(productModel.getBrandId())))
										.append(File.separatorChar).append(productModel.getStyle())
										.append("_").append(count)
										.append(origName.substring(origName.lastIndexOf(".")));
							// 处理物品大图
							String bigPath = fileRepository.saveToTarget(
									multipartFile.getInputStream(), newFileName.toString());
							if (StringUtils.isNotBlank(bigPath)) {
								bigImgUrls.add(File.separatorChar + bigPath);
							}
							// 处理物品小图
							String litPath = ImageUtil.createSquareImage(pathFileUpload
									+ File.separatorChar + bigPath, 300, "lit");
							if (StringUtils.isNotBlank(litPath)) {
								litImgUrls.add(File.separatorChar
										+ prefixMap.get(String.valueOf(productModel.getBrandId()))
										+ File.separatorChar + litPath.replace("_lit", ""));
							}
						}
						count++;
					}
				}
				productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
				List<String> coverSmall = new ArrayList<String>();
				coverSmall.add(litImgUrls.get(0));
				productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
