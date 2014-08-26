
package com.flyhz.shop.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
import com.flyhz.shop.dto.ProductPageDto;
import com.flyhz.shop.dto.ProductParamDto;
import com.flyhz.shop.enums.BrandPrefixEnum;
import com.flyhz.shop.enums.CategoryPrefixEnum;
import com.flyhz.shop.persistence.dao.BrandDao;
import com.flyhz.shop.persistence.dao.ProductDao;
import com.flyhz.shop.persistence.entity.ProductLogModel;
import com.flyhz.shop.persistence.entity.ProductModel;
import com.flyhz.shop.service.ProductService;

public class ProductServiceImpl implements ProductService {
	@Resource
	private ProductDao				productDao;
	@Resource
	private BrandDao				brandDao;
	@Resource
	private FileRepository			fileRepository;
	@Resource
	private SolrData				solrData;

	private String					pathFileUpload;
	private static final String		OPERATE_ADD			= "add";
	private static final String		OPERATE_EDIT		= "edit";
	private static final String		OPERATE_DELETE		= "delete";
	private static final String		COVER_SMALL_IS_DEL	= "1";
	private static final String		NULL_IMGS			= "[]";
	private static final long		FILE_MIN_LENGTH		= 51200;				// 产品图片最小为100K
	private static AtomicInteger	IMAGE_COUNT			= new AtomicInteger(0);

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
		productModel.setCurrency(productParamDto.getCurrency());
		productModel.setOriginprice(productParamDto.getOriginprice());
		productModel.setDiscountprice(productParamDto.getDiscountprice());
		int maxStyle = productDao.getMaxStyle();
		productModel.setStyle(String.valueOf(maxStyle + 1));
		// 处理颜色图片
		dispostColorimg(productModel, productParamDto);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel);
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
		productModel.setCurrency(productParamDto.getCurrency());
		productModel.setOriginprice(productParamDto.getOriginprice());
		productModel.setDiscountprice(productParamDto.getDiscountprice());
		// 处理颜色图片
		dispostColorimg(productModel, productParamDto);
		// 处理商品图片
		dispostProductImgs(productParamDto.getImgs(), productModel, productParamDto);
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
		// 查询产品数量并重新截图
		List<ProductModel> productModels = productDao.getProductsByStartAndEnd(productPageDto);
		if (productModels != null && !productModels.isEmpty()) {
			for (ProductModel productModel : productModels) {
				if (productModel != null && StringUtils.isNotBlank(productModel.getImgs())
						&& !NULL_IMGS.equals(productModel.getImgs())) {
					// 存储未更改前product数据
					ProductLogModel productLogModel = new ProductLogModel();
					productLogModel.setBeforeInfo(JSONUtil.getEntity2Json(productModel));
					disposeProductImgSrcRe(productModel);
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

	private void dispostColorimg(ProductModel productModel, ProductParamDto productParamDto) {
		if (productModel != null && productParamDto != null
				&& productParamDto.getColorimg() != null
				&& StringUtils.isBlank(productParamDto.getOldColorimg())) {
			MultipartFile file = productParamDto.getColorimg();
			String origName = file.getOriginalFilename();
			if (StringUtils.isNotBlank(origName)) {
				StringBuffer newFileName = new StringBuffer();
				newFileName.append(BrandPrefixEnum.getBrandPrefix(productParamDto.getBrandId()))
							.append(File.separatorChar).append("color").append(File.separatorChar)
							.append(productModel.getStyle())
							.append(origName.substring(origName.lastIndexOf(".")));
				try {
					// 保存颜色图到指定文件路径里
					String filename = fileRepository.saveToTarget(file.getInputStream(),
							newFileName.toString());
					productModel.setColorimg(filename);
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
			// 定义图片保存路径前缀
			StringBuffer prefixBuffer = new StringBuffer();
			prefixBuffer.append(File.separatorChar)
						.append(BrandPrefixEnum.getBrandPrefix(productModel.getBrandId()))
						.append(File.separatorChar)
						.append(CategoryPrefixEnum.getCategoryPrefix(productModel.getCategoryId()))
						.append(File.separatorChar);
			try {
				int count = 0;
				for (MultipartFile multipartFile : files) {
					String origName = multipartFile.getOriginalFilename();
					if (StringUtils.isNotBlank(origName)
							&& multipartFile.getSize() >= FILE_MIN_LENGTH) {
						if (IMAGE_COUNT.intValue() > 99) {
							IMAGE_COUNT = new AtomicInteger(0);
						}
						StringBuffer newFileName = new StringBuffer();
						newFileName.append(prefixBuffer.toString())
									.append(System.currentTimeMillis())
									.append(IMAGE_COUNT.getAndIncrement())
									.append(origName.substring(origName.lastIndexOf(".")));
						// 处理物品大图
						String bigPath = fileRepository.saveToTarget(
								multipartFile.getInputStream(), newFileName.toString());
						if (StringUtils.isNotBlank(bigPath)) {
							bigImgUrls.add(newFileName.toString());
						}
						// 处理物品小图
						if (count == 0) {
							String litPath = disposeLitImgs(productModel.getBrandId(),
									pathFileUpload + newFileName.toString());
							List<String> coverSmall = new ArrayList<String>();
							if (StringUtils.isNotBlank(litPath)) {
								coverSmall.add(prefixBuffer.toString() + litPath);
							}
							productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
						}
						// 缩放图片
						String scalePath = ImageUtil.zoomInScale(
								pathFileUpload + newFileName.toString(), 500, "scale");
						if (StringUtils.isNotBlank(scalePath)) {
							litImgUrls.add(prefixBuffer.toString() + scalePath);
						}
						count++;
					}
				}
				productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
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
					// 定义图片保存路径前缀
					StringBuffer prefixBuffer = new StringBuffer();
					prefixBuffer.append(File.separatorChar)
								.append(BrandPrefixEnum.getBrandPrefix(productModel.getBrandId()))
								.append(File.separatorChar)
								.append(CategoryPrefixEnum.getCategoryPrefix(productModel.getCategoryId()))
								.append(File.separatorChar);
					int count = 0;
					for (MultipartFile multipartFile : files) {
						String origName = multipartFile.getOriginalFilename();
						if (StringUtils.isNotBlank(origName)
								&& multipartFile.getSize() >= FILE_MIN_LENGTH) {
							if (IMAGE_COUNT.intValue() > 99) {
								IMAGE_COUNT = new AtomicInteger(0);
							}
							StringBuffer newFileName = new StringBuffer();
							newFileName.append(prefixBuffer.toString())
										.append(System.currentTimeMillis())
										.append(IMAGE_COUNT.getAndIncrement())
										.append(origName.substring(origName.lastIndexOf(".")));
							// 处理物品大图
							String bigPath = fileRepository.saveToTarget(
									multipartFile.getInputStream(), newFileName.toString());
							if (StringUtils.isNotBlank(bigPath)) {
								bigImgUrls.add(newFileName.toString());
							}
							// 处理物品小图
							if (count == 0
									&& COVER_SMALL_IS_DEL.equals(productParamDto.getCoverSmallDel())) {
								String filePath = null;
								if (bigImgUrls != null && !bigImgUrls.isEmpty()) {
									filePath = pathFileUpload + bigImgUrls.get(0);
								} else {
									filePath = pathFileUpload + newFileName.toString();
								}
								String litPath = disposeLitImgs(productModel.getBrandId(), filePath);
								List<String> coverSmall = new ArrayList<String>();
								if (StringUtils.isNotBlank(litPath)) {
									coverSmall.add(prefixBuffer.toString() + litPath);
								}
								productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
							}
							// 缩放图片
							String scalePath = ImageUtil.zoomInScale(
									pathFileUpload + newFileName.toString(), 500, "scale");
							if (StringUtils.isNotBlank(scalePath)) {
								litImgUrls.add(prefixBuffer.toString() + scalePath);
							}
							count++;
						}
					}
				}
				productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
				productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
				// 如果全部图片删除，删除封面图片
				if (bigImgUrls.isEmpty()) {
					productModel.setCoverSmall(JSONUtil.getEntity2Json(bigImgUrls));
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
					// litPath = ImageUtil.createSquareImage(filePath, 300,
					// "lit",
					// ImageUtil.TYPE_BOTTOM);
					litPath = ImageUtil.createSquareImage(filePath, 300, "lit");
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
	private void disposeProductImgSrcRe(ProductModel productModel) {
		if (productModel != null && StringUtils.isNotBlank(productModel.getImgs())
				&& !NULL_IMGS.equals(productModel.getImgs())) {
			List<String> productSrcImgs = JSONUtil.getJson2EntityList(productModel.getImgs(),
					ArrayList.class, String.class);
			try {
				if (productSrcImgs != null && !productSrcImgs.isEmpty()) {
					List<String> bigImgUrls = new ArrayList<String>();
					List<String> litImgUrls = new ArrayList<String>();
					// 定义图片保存路径前缀
					StringBuffer prefixBuffer = new StringBuffer();
					prefixBuffer.append(File.separatorChar)
								.append(BrandPrefixEnum.getBrandPrefix(productModel.getBrandId()))
								.append(File.separatorChar)
								.append(CategoryPrefixEnum.getCategoryPrefix(productModel.getCategoryId()))
								.append(File.separatorChar);
					// long fileMinLen = 81920;// 图片最小80K
					// 循环处理产品原图片
					int count = 0;
					String path = "/root/www/static/smile";
					for (String productSrcImg : productSrcImgs) {
						if (StringUtils.isNotBlank(productSrcImg)) {
							// 获得产品原图片文件
							File imgFile = new File(path + "/product" + productSrcImg);
							// if (imgFile.exists() && imgFile.length() >=
							// fileMinLen) {
							if (imgFile.exists()) {
								if (IMAGE_COUNT.intValue() > 99) {
									IMAGE_COUNT = new AtomicInteger(0);
								}
								StringBuffer newFileName = new StringBuffer();
								newFileName.append(prefixBuffer.toString())
											.append(System.currentTimeMillis())
											.append(IMAGE_COUNT.getAndIncrement())
											.append(productSrcImg.substring(productSrcImg.lastIndexOf(".")));
								// 处理物品大图
								String bigPath = fileRepository.saveToTarget(new FileInputStream(
										imgFile), newFileName.toString());
								if (StringUtils.isNotBlank(bigPath)) {
									bigImgUrls.add(newFileName.toString());
								}
								// 处理物品小图
								if (count == 0) {
									String litPath = disposeLitImgs(productModel.getBrandId(),
											pathFileUpload + newFileName.toString());
									List<String> coverSmall = new ArrayList<String>();
									if (StringUtils.isNotBlank(litPath)) {
										coverSmall.add(prefixBuffer.toString() + litPath);
									}
									productModel.setCoverSmall(JSONUtil.getEntity2Json(coverSmall));
								}
								// 缩放图片
								String scalePath = ImageUtil.zoomInScale(pathFileUpload
										+ newFileName.toString(), 500, "scale");
								if (StringUtils.isNotBlank(scalePath)) {
									litImgUrls.add(prefixBuffer.toString() + scalePath);
								}
								count++;
							}
						}
					}
					productModel.setImgs(JSONUtil.getEntity2Json(bigImgUrls));
					productModel.setCover(JSONUtil.getEntity2Json(litImgUrls));
					// 如果全部图片删除，删除封面图片
					if (bigImgUrls.isEmpty()) {
						productModel.setCoverSmall(JSONUtil.getEntity2Json(bigImgUrls));
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// 获得yyyyMMdd格式日期
	public String getCurrentDate() {
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String currentDateTime = fmt.format(new Date());
		return currentDateTime;
	}

	public static void main(String[] args) {
		// String url =
		// "http://maliprod.alipay.com/w/trade_pay.do?alipay_trade_no=2014062611001001380023157848&s_id=736d2ca9b7756f2a408ed0bb85617674&tcode=eyJiaXpPcmRlcklkcyI6IjcxMDI5NTUyMjc3MzY1OSIsInR5cGUiOiIzIiwiYnV5ZXJJZCI6IjE3MTYyMjU5MzYifQ%3D%3D&pay_order_id=710295522773659&refer=tbc";
		// System.out.println(url.indexOf("pay_order_id="));
		// System.out.println(url.substring(url.indexOf("pay_order_id=") + 13,
		// url.lastIndexOf("&")));
		SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
		String currentDateTime = fmt.format(new Date());
		System.out.println(currentDateTime);
	}
}
