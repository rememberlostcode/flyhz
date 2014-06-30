
package com.flyhz.shop.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
import com.flyhz.shop.dto.BrandBuildDto;
import com.flyhz.shop.dto.ProductBuildDto;
import com.flyhz.shop.dto.ProductCmsDto;
import com.flyhz.shop.dto.ProductPageDto;
import com.flyhz.shop.dto.ProductParamDto;
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.ProductLogModel;
import com.flyhz.shop.persistence.entity.ProductModel;
import com.flyhz.shop.service.ProductService;

public class ProductServiceImpl implements ProductService {
	@Resource
	private ProductDao			productDao;
	@Resource
	private BrandDao			brandDao;
	@Resource
	private FileRepository		fileRepository;
	@Resource
	private SolrData			solrData;

	private String				pathFileUpload;
	private static final String	OPERATE_ADD			= "add";
	private static final String	OPERATE_EDIT		= "edit";
	private static final String	OPERATE_DELETE		= "delete";
	private static final String	COVER_SMALL_IS_DEL	= "1";
	private static final String	NULL_IMGS			= "[]";

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
	public void addProduct(Integer userId, ProductParamDto productParamDto)
			throws ValidateException {
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
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
		productModel.setName(productParamDto.getName().trim());
		productModel.setBrandId(productParamDto.getBrandId());
		productModel.setBrandstyle(productParamDto.getBrandstyle().trim());
		productModel.setCategoryId(productParamDto.getCategoryId());
		productModel.setColor(productParamDto.getColor());
		productModel.setCreator(String.valueOf(userId));
		productModel.setDataSrc(productParamDto.getDataSrc());
		productModel.setOffShelf(productParamDto.getOffShelf());
		productModel.setSizedesc(productParamDto.getSizedesc());
		if (StringUtils.isNotBlank(productParamDto.getDescription())) {
			productModel.setDescription(productParamDto.getDescription());
		} else {
			productModel.setDescription(productModel.getName().trim());
		}
		productModel.setGmtCreate(new Date());
		productModel.setGmtModify(new Date());
		productModel.setForeighprice(productParamDto.getForeighprice());
		productModel.setRecommendprice(productParamDto.getRecommendprice());
		// productModel.setLocalprice(productParamDto.getForeighprice());
		productModel.setPurchasingprice(productParamDto.getRecommendprice());
		int maxStyle = productDao.getMaxStyle();
		productModel.setStyle(String.valueOf(maxStyle + 1));
		// 获得品牌名称
		String brandName = null;
		if (productParamDto.getBrandId() != null) {
			brandName = brandDao.getBrandNameById(productParamDto.getBrandId());
		}
		// 处理颜色图片
		dispostColorimg(productParamDto.getColorimg(), productModel, productParamDto, brandName);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel, brandName);
		productDao.addProduct(productModel);
		// 记录产品操作日志
		ProductLogModel productLogModel = new ProductLogModel();
		productLogModel.setProductId(productModel.getId());
		productLogModel.setUserId(userId);
		productLogModel.setOperate(OPERATE_ADD);
		productLogModel.setGmtModify(new Date());
		productLogModel.setAfterInfo(JSONUtil.getEntity2Json(productModel));
		productDao.addProductLog(productLogModel);
		// builder数据到Solr中
		ProductBuildDto productBuildDto = productDao.getProductBuildDtoById(productModel.getId());
		solrData.submitProduct(productBuildDto);
	}

	@Override
	public void deleteProduct(Integer userId, Integer productId) throws ValidateException {
		if (productId == null) {
			throw new ValidateException("产品不存在");
		}
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
		ProductModel productModel = productDao.getModelById(productId);
		if (productModel != null) {
			productDao.deleteProduct(productId);
			// 记录产品操作日志
			ProductLogModel productLogModel = new ProductLogModel();
			productLogModel.setProductId(productId);
			productLogModel.setUserId(userId);
			productLogModel.setOperate(OPERATE_DELETE);
			productLogModel.setGmtModify(new Date());
			productLogModel.setBeforeInfo(JSONUtil.getEntity2Json(productModel));
			productDao.addProductLog(productLogModel);
			// 删除索引中的产品
			solrData.removeProduct(String.valueOf(productId));
		}
	}

	@Override
	public void batchDelProducts(Integer userId, String productIds) throws ValidateException {
		if (StringUtils.isBlank(productIds)) {
			throw new ValidateException("产品不存在");
		}
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
		String[] productIdsArray = productIds.split(",");
		for (int i = 0; i < productIdsArray.length; i++) {
			Integer productId = Integer.parseInt(productIdsArray[i]);
			if (productId != null) {
				ProductModel productModel = productDao.getModelById(productId);
				if (productModel != null) {
					productDao.deleteProduct(productId);
					// 记录产品操作日志
					ProductLogModel productLogModel = new ProductLogModel();
					productLogModel.setProductId(productId);
					productLogModel.setUserId(userId);
					productLogModel.setOperate(OPERATE_DELETE);
					productLogModel.setGmtModify(new Date());
					productLogModel.setBeforeInfo(JSONUtil.getEntity2Json(productModel));
					productDao.addProductLog(productLogModel);
					// 删除索引中的产品
					solrData.removeProduct(String.valueOf(productId));
				}
			}
		}
	}

	@Override
	public void editProduct(Integer userId, ProductParamDto productParamDto)
			throws ValidateException {
		if (userId == null) {
			throw new ValidateException("您尚未登录");
		}
		if (productParamDto == null || productParamDto.getId() == null) {
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
		ProductModel productModel = productDao.getModelById(productParamDto.getId());
		if (productModel == null) {
			throw new ValidateException("产品不存在");
		}
		// 存储未更改前product数据
		ProductLogModel productLogModel = new ProductLogModel();
		productLogModel.setBeforeInfo(JSONUtil.getEntity2Json(productModel));
		// 生成产品Model对象
		productModel.setName(productParamDto.getName().trim());
		productModel.setBrandId(productParamDto.getBrandId());
		productModel.setBrandstyle(productParamDto.getBrandstyle().trim());
		productModel.setCategoryId(productParamDto.getCategoryId());
		productModel.setColor(productParamDto.getColor());
		productModel.setOffShelf(productParamDto.getOffShelf());
		productModel.setSizedesc(productParamDto.getSizedesc());
		if (StringUtils.isNotBlank(productParamDto.getDescription())) {
			productModel.setDescription(productParamDto.getDescription());
		} else {
			productModel.setDescription(productModel.getName().trim());
		}
		productModel.setGmtModify(new Date());
		// productModel.setLocalprice(productParamDto.getForeighprice());
		productModel.setForeighprice(productParamDto.getForeighprice());
		productModel.setRecommendprice(productParamDto.getRecommendprice());
		productModel.setPurchasingprice(productParamDto.getRecommendprice());
		// 获得品牌名称
		String brandName = null;
		if (productParamDto.getBrandId() != null) {
			brandName = brandDao.getBrandNameById(productParamDto.getBrandId());
		}
		// 处理颜色图片
		dispostColorimg(productParamDto.getColorimg(), productModel, productParamDto, brandName);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel, productParamDto, brandName);
		productDao.editProduct(productModel);
		// 记录产品操作日志
		productLogModel.setProductId(productModel.getId());
		productLogModel.setUserId(userId);
		productLogModel.setOperate(OPERATE_EDIT);
		productLogModel.setGmtModify(new Date());
		productLogModel.setAfterInfo(JSONUtil.getEntity2Json(productModel));
		productDao.addProductLog(productLogModel);
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
	public List<ProductCmsDto> getProductCmsDtosByPage(Pager pager, ProductModel product) {
		if (pager == null || product == null) {
			return null;
		}
		// 处理生成分页参数
		ProductPageDto productPageDto = new ProductPageDto();
		// 设置搜索参数
		if (StringUtils.isNotBlank(product.getName())) {
			StringBuffer nameBuffer = new StringBuffer();
			nameBuffer.append("%").append(product.getName()).append("%");
			productPageDto.setName(nameBuffer.toString());
		}
		if (StringUtils.isNotBlank(product.getBrandstyle())) {
			productPageDto.setBrandstyle(product.getBrandstyle());
		}
		if (product.getBrandId() != null) {
			productPageDto.setBrandId(product.getBrandId());
		}
		if (product.getCategoryId() != null) {
			productPageDto.setCategoryId(product.getCategoryId());
		}
		// 定义分页参数
		int currentPage = pager.getCurrentPage();
		int pageSize = pager.getPageSize();
		// 查询产品总数量
		int count = productDao.getProductCmsDtosPageCount(productPageDto);
		// pager设置总页数和总数量
		pager.setTotalRowsAmount(count);
		if (count % pageSize == 0) {
			pager.setTotalPages(count / pageSize);
		} else {
			pager.setTotalPages(count / pageSize + 1);
		}
		// 重新定义当前页
		if (currentPage > pager.getTotalPages()) {
			currentPage = 1;
			pager.setCurrentPage(currentPage);
		}
		// 设置分页参数
		productPageDto.setStart((currentPage - 1) * pageSize);
		productPageDto.setPagesize(pageSize);
		// 查询产品列表
		List<ProductCmsDto> productCmsDtos = productDao.getProductCmsDtosPage(productPageDto);
		if (productCmsDtos != null && !productCmsDtos.isEmpty()) {
			for (ProductCmsDto productCmsDto : productCmsDtos) {
				if (StringUtils.isNotBlank(productCmsDto.getCover())) {
					List<String> productImgs = JSONUtil.getJson2EntityList(
							productCmsDto.getCover(), ArrayList.class, String.class);
					productCmsDto.setProductImgs(productImgs);
				}
				if (StringUtils.isNotBlank(productCmsDto.getCoverSmall())) {
					List<String> productAppImgs = JSONUtil.getJson2EntityList(
							productCmsDto.getCoverSmall(), ArrayList.class, String.class);
					productCmsDto.setAppImages(productAppImgs);
				}
			}
		}
		return productCmsDtos;
	}

	@Override
	public List<ProductCmsDto> getProductCmsDtosByPage(Pager pager) {
		List<ProductCmsDto> productCmsDtos = productDao.getPageProductCmsDtos(pager);
		if (productCmsDtos != null && !productCmsDtos.isEmpty()) {
			for (ProductCmsDto productCmsDto : productCmsDtos) {
				if (StringUtils.isNotBlank(productCmsDto.getCover())) {
					List<String> productImgs = JSONUtil.getJson2EntityList(
							productCmsDto.getCover(), ArrayList.class, String.class);
					productCmsDto.setProductImgs(productImgs);
				}
				if (StringUtils.isNotBlank(productCmsDto.getCoverSmall())) {
					List<String> productAppImgs = JSONUtil.getJson2EntityList(
							productCmsDto.getCoverSmall(), ArrayList.class, String.class);
					productCmsDto.setAppImages(productAppImgs);
				}
			}
		}
		return productCmsDtos;
	}

	@Override
	public void refreshProductImgs(Integer userId, Integer start, Integer end) {
		ProductPageDto productPageDto = new ProductPageDto();
		// 设置起始ID
		if (start != null) {
			productPageDto.setStart(start);
		}
		// 设置结束ID
		if (end != null) {
			productPageDto.setEnd(end);
		}
		// 查询全部品牌列表
		List<BrandBuildDto> brandBuildDtos = brandDao.getBrandBuildDtoList();
		// 查询产品数量并重新截图
		List<ProductModel> productModels = productDao.getProductsByStartAndEnd(productPageDto);
		if (productModels != null && !productModels.isEmpty()) {
			for (ProductModel productModel : productModels) {
				if (productModel != null && StringUtils.isNotBlank(productModel.getImgs())
						&& !NULL_IMGS.equals(productModel.getImgs())) {
					String brandName = getBrandNameById(brandBuildDtos, productModel.getBrandId());
					// 存储未更改前product数据
					ProductLogModel productLogModel = new ProductLogModel();
					productLogModel.setBeforeInfo(JSONUtil.getEntity2Json(productModel));
					disposeProductImgSrcRe(productModel, brandName);
					productDao.editProduct(productModel);
					// 记录产品操作日志
					productLogModel.setProductId(productModel.getId());
					productLogModel.setUserId(userId);
					productLogModel.setOperate(OPERATE_EDIT);
					productLogModel.setGmtModify(new Date());
					productLogModel.setAfterInfo(JSONUtil.getEntity2Json(productModel));
					productDao.addProductLog(productLogModel);
					// builder数据到Solr中
					ProductBuildDto productBuildDto = productDao.getProductBuildDtoById(productModel.getId());
					solrData.submitProduct(productBuildDto);
				}
			}
		}
	}

	private void dispostColorimg(MultipartFile file, ProductModel productModel,
			ProductParamDto productParamDto, String brandName) {
		if (file != null && productModel != null && productParamDto != null
				&& StringUtils.isNotBlank(brandName)
				&& StringUtils.isBlank(productParamDto.getOldColorimg())) {
			String origName = file.getOriginalFilename();
			if (StringUtils.isNotBlank(origName)) {
				StringBuffer newFileName = new StringBuffer();
				newFileName.append(brandName).append(File.separatorChar)
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

	private void dispostProductImgs(List<MultipartFile> files, ProductModel productModel,
			String brandName) {
		if (files != null && !files.isEmpty() && productModel != null
				&& StringUtils.isNotBlank(brandName)) {
			List<String> bigImgUrls = new ArrayList<String>();
			List<String> litImgUrls = new ArrayList<String>();
			try {
				int count = 0;
				String prefix = File.separatorChar + brandName + File.separatorChar;
				for (MultipartFile multipartFile : files) {
					String origName = multipartFile.getOriginalFilename();
					if (StringUtils.isNotBlank(origName)) {
						StringBuffer newFileName = new StringBuffer();
						newFileName.append(brandName).append(File.separatorChar)
									.append(productModel.getStyle()).append("_")
									.append(System.currentTimeMillis())
									.append(origName.substring(origName.lastIndexOf(".")));
						// 处理物品大图
						String bigPath = fileRepository.saveToTarget(
								multipartFile.getInputStream(), newFileName.toString());
						if (StringUtils.isNotBlank(bigPath)) {
							bigImgUrls.add(File.separatorChar + bigPath);
						}
						// 处理物品小图
						if (count == 0) {
							String litPath = disposeLitImgs(productModel.getBrandId(),
									pathFileUpload + File.separatorChar + bigPath);
							List<String> coverSmall = new ArrayList<String>();
							if (StringUtils.isNotBlank(litPath)) {
								coverSmall.add(prefix + litPath);
							}
							productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
						}
						// 缩放图片
						String scalePath = ImageUtil.zoomInScale(pathFileUpload
								+ File.separatorChar + bigPath, 500, "scale");
						if (StringUtils.isNotBlank(scalePath)) {
							litImgUrls.add(prefix + scalePath);
						}
						count++;
					}
				}
				if (bigImgUrls != null && !bigImgUrls.isEmpty()) {
					productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				}
				if (litImgUrls != null && !litImgUrls.isEmpty()) {
					productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void dispostProductImgs(List<MultipartFile> files, ProductModel productModel,
			ProductParamDto productParamDto, String brandName) {
		if (productModel != null && productParamDto != null && StringUtils.isNotBlank(brandName)) {
			List<String> bigImgUrls = productParamDto.getProductSrcImgs() == null ? new ArrayList<String>()
					: productParamDto.getProductSrcImgs();
			List<String> litImgUrls = productParamDto.getProductImgs() == null ? new ArrayList<String>()
					: productParamDto.getProductImgs();
			try {
				String prefix = File.separatorChar + brandName + File.separatorChar;
				if (files != null && !files.isEmpty()) {
					int count = 0;
					for (MultipartFile multipartFile : files) {
						String origName = multipartFile.getOriginalFilename();
						if (StringUtils.isNotBlank(origName)) {
							StringBuffer newFileName = new StringBuffer();
							newFileName.append(brandName).append(File.separatorChar)
										.append(productModel.getStyle()).append("_")
										.append(System.currentTimeMillis())
										.append(origName.substring(origName.lastIndexOf(".")));
							// 处理物品大图
							String bigPath = fileRepository.saveToTarget(
									multipartFile.getInputStream(), newFileName.toString());
							if (StringUtils.isNotBlank(bigPath)) {
								bigImgUrls.add(File.separatorChar + bigPath);
							}
							// 处理物品小图
							if (count == 0
									&& COVER_SMALL_IS_DEL.equals(productParamDto.getCoverSmallDel())) {
								String filePath = null;
								if (bigImgUrls != null && !bigImgUrls.isEmpty()) {
									filePath = pathFileUpload + bigImgUrls.get(0);
								} else {
									filePath = pathFileUpload + File.separatorChar + bigPath;
								}
								String litPath = disposeLitImgs(productModel.getBrandId(), filePath);
								List<String> coverSmall = new ArrayList<String>();
								if (StringUtils.isNotBlank(litPath)) {
									coverSmall.add(prefix + litPath);
								}
								productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
							}
							// 缩放图片
							String scalePath = ImageUtil.zoomInScale(pathFileUpload
									+ File.separatorChar + bigPath, 500, "scale");
							if (StringUtils.isNotBlank(scalePath)) {
								litImgUrls.add(prefix + scalePath);
							}
							count++;
						}
					}
				}
				if (bigImgUrls != null && !bigImgUrls.isEmpty()) {
					productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				}
				if (litImgUrls != null && !litImgUrls.isEmpty()) {
					productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 处理产品封面图片截图
	private String disposeLitImgs(Integer brandId, String filePath) {
		if (StringUtils.isNotBlank(filePath)) {
			String litPath = null;
			try {
				if (brandId != null && brandId.equals(9)) {
					litPath = ImageUtil.createSquareImage(filePath, 300, "lit",
							ImageUtil.TYPE_BOTTOM);
				} else {
					litPath = ImageUtil.createSquareImage(filePath, 300, "lit");
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			return litPath;
		}
		return null;
	}

	// 重新读取产品原图片并处理
	private void disposeProductImgSrcRe(ProductModel productModel, String brandName) {
		if (productModel != null && StringUtils.isNotBlank(productModel.getImgs())
				&& !NULL_IMGS.equals(productModel.getImgs()) && StringUtils.isNotBlank(brandName)) {
			List<String> productSrcImgs = JSONUtil.getJson2EntityList(productModel.getImgs(),
					ArrayList.class, String.class);
			try {
				String prefix = File.separatorChar + brandName + File.separatorChar;
				if (productSrcImgs != null && !productSrcImgs.isEmpty()) {
					List<String> bigImgUrls = new ArrayList<String>();
					List<String> litImgUrls = new ArrayList<String>();
					// 循环处理产品原图片
					int count = 0;
					for (String productSrcImg : productSrcImgs) {
						if (StringUtils.isNotBlank(productSrcImg)) {
							StringBuffer newFileName = new StringBuffer();
							newFileName.append(brandName)
										.append(File.separatorChar)
										.append(productModel.getStyle())
										.append("_")
										.append(System.currentTimeMillis())
										.append(productSrcImg.substring(productSrcImg.lastIndexOf(".")));
							// 获得产品原图片文件
							File imgFile = new File(pathFileUpload + File.separatorChar
									+ productSrcImg);
							if (imgFile.exists()) {
								// 处理物品大图
								String bigPath = fileRepository.saveToTarget(new FileInputStream(
										imgFile), newFileName.toString());
								if (StringUtils.isNotBlank(bigPath)) {
									bigImgUrls.add(File.separatorChar + bigPath);
								}
								// 处理物品小图
								if (count == 0) {
									String litPath = disposeLitImgs(productModel.getBrandId(),
											pathFileUpload + File.separatorChar + bigPath);
									List<String> coverSmall = new ArrayList<String>();
									if (StringUtils.isNotBlank(litPath)) {
										coverSmall.add(prefix + litPath);
									}
									productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
								}
								// 缩放图片
								String scalePath = ImageUtil.zoomInScale(pathFileUpload
										+ File.separatorChar + bigPath, 500, "scale");
								if (StringUtils.isNotBlank(scalePath)) {
									litImgUrls.add(prefix + scalePath);
								}
								count++;

							}
						}
					}
					if (bigImgUrls != null && !bigImgUrls.isEmpty()) {
						productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
					}
					if (litImgUrls != null && !litImgUrls.isEmpty()) {
						productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 品牌列表中获得品牌名称
	private String getBrandNameById(List<BrandBuildDto> brandBuildDtos, Integer brandId) {
		if (brandId != null && brandBuildDtos != null && !brandBuildDtos.isEmpty()) {
			for (BrandBuildDto brandBuildDto : brandBuildDtos) {
				if (brandBuildDto != null && brandId.equals(brandBuildDto.getId())) {
					return brandBuildDto.getName();
				}
			}
		}
		return null;
	}
}
