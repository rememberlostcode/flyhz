
package com.flyhz.shop.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.flyhz.framework.lang.RedisRepository;
import com.flyhz.framework.lang.ValidateException;
import com.flyhz.framework.util.StringUtil;
import com.flyhz.shop.dto.CartItemDto;
import com.flyhz.shop.dto.CartItemParamDto;
import com.flyhz.shop.dto.DiscountDto;
import com.flyhz.shop.dto.ProductDto;
import com.flyhz.shop.persistence.dao.CartItemDao;
import com.flyhz.shop.persistence.dao.DiscountDao;
import com.flyhz.shop.persistence.dao.UserDao;
import com.flyhz.shop.persistence.entity.CartitemModel;
import com.flyhz.shop.persistence.entity.DiscountModel;
import com.flyhz.shop.service.ShoppingCartService;

@Service
public class ShoopingCartServiceImpl implements ShoppingCartService {
	@Resource
	private CartItemDao		cartItemDao;
	@Resource
	private RedisRepository	redisRepository;
	@Resource
	private UserDao			userDao;
	@Resource
	private DiscountDao		discountDao;

	@Override
	public void addItem(Integer userId, Integer productId, Byte qty, Integer discountId)
			throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 商品ID不能为空
		if (productId == null) {
			throw new ValidateException(101021);
		}
		// 商品数量错误
		if (qty == null || qty <= 0) {
			throw new ValidateException(101022);
		}
		// 查询购物车是否已有该物品
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setProductId(productId);
		cartitemModel.setUserId(userId);
		CartitemModel cartitemModelNew = cartItemDao.getCartItemByProductId(cartitemModel);
		
		// 购物车无此商品
		if (cartitemModelNew == null) {
			cartitemModel.setDiscountId(discountId);
			cartitemModel.setGmtCreate(new Date());
			cartitemModel.setGmtModify(new Date());
			cartitemModel.setQty(qty);
			cartItemDao.insertCartItem(cartitemModel);
		} else {
			// 购物车有此商品
			qty = (byte) (cartitemModelNew.getQty() + qty);
			cartitemModelNew.setQty(qty);
			cartitemModelNew.setGmtModify(new Date());
			cartitemModelNew.setDiscountId(discountId);
			cartItemDao.updateCartItem(cartitemModelNew);
		}

//		UserDto user = userDao.getUserById(userId);
//		if (user != null && user.getId() != null && user.getRegistrationID() != null) {
//			JPush jpush = new JPush();
//			Map<String, String> map = new HashMap<String, String>();
//			map.put("qty", qty+"");
//			jpush.sendAndroidMessageWithRegistrationID("您的购物车有新的商品！",
//					map, user.getRegistrationID());
//		}
	}

	@Override
	public void removeItem(Integer userId, Integer itemId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 购物车商品ID不能为空
		if (itemId == null) {
			throw new ValidateException(101023);
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartItemDao.deleteCartItem(cartitemModel);
	}

	@Override
	public List<CartItemDto> listItems(Integer userId) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		List<CartitemModel> cartItems = cartItemDao.getCartItemList(cartitemModel);
		List<CartItemDto> cartItemDtos = new ArrayList<CartItemDto>();
		if (cartItems != null && !cartItems.isEmpty()) {
			for (CartitemModel cartitemModelIte : cartItems) {
				cartItemDtos.add(getCartItemDto(cartitemModelIte));
			}
		}
		return cartItemDtos;
	}

	/**
	 * 处理CartitemModel，返回CartItemDto对象
	 * 
	 * @param cartitemModel
	 * @return CartItemDto
	 */
	private CartItemDto getCartItemDto(CartitemModel cartitemModel) {
		if (cartitemModel != null) {
			CartItemDto cartItemDto = new CartItemDto();
			cartItemDto.setId(cartitemModel.getId());
			cartItemDto.setQty((short) cartitemModel.getQty());
			ProductDto productDto = null;
			try {
				productDto = redisRepository.getProductFromRedis(String.valueOf(cartitemModel.getProductId()));
			} catch (ValidateException e) {
				e.printStackTrace();
			}
			if (productDto != null) {
				if (productDto.getPurchasingPrice() != null) {
					BigDecimal detailTotal = productDto.getPurchasingPrice().multiply(
							BigDecimal.valueOf(cartItemDto.getQty()));
					cartItemDto.setTotal(detailTotal);

					// 处理打折情况
					if (cartitemModel.getDiscountId() != null) {
						DiscountModel discountModel = discountDao.getModelById(cartitemModel.getDiscountId());
						if (discountModel != null && discountModel.getDiscount() != 0) {
							DiscountDto discount = new DiscountDto();
							discount.setId(discountModel.getId());
							discount.setDiscount(discountModel.getDiscount());
							double val = StringUtil.div(discountModel.getDiscount(), 10, 2);
							detailTotal = detailTotal.multiply(BigDecimal.valueOf(val));
							discount.setDp(detailTotal);
							cartItemDto.setDiscount(discount);
						}
					}
				}
			}
			cartItemDto.setProduct(productDto);
			return cartItemDto;
		}
		return null;
	}

	@Override
	public CartItemDto setQty(Integer userId, Integer itemId, Byte qty) throws ValidateException {
		// 登陆用户ID不能为空
		if (userId == null) {
			throw new ValidateException(101002);
		}
		// 购物车商品ID不能为空
		if (itemId == null) {
			throw new ValidateException(101023);
		}
		// 商品数量错误
		if (qty == null || qty <= 0) {
			throw new ValidateException(101022);
		}
		CartitemModel cartitemModel = new CartitemModel();
		cartitemModel.setUserId(userId);
		cartitemModel.setId(itemId);
		cartitemModel = cartItemDao.getCartItem(cartitemModel);
		// 购物车商品不能为空
		if (cartitemModel == null) {
			throw new ValidateException(101023);
		}
		cartitemModel.setQty(qty);
		cartitemModel.setGmtModify(new Date());
		cartItemDao.updateCartItem(cartitemModel);
		return getCartItemDto(cartitemModel);
	}

	@Override
	public String[] listItemsByUserIdAndIds(CartItemParamDto cartItemParam) {
		if (cartItemParam == null || cartItemParam.getUserId() == null
				|| cartItemParam.getItemIds() == null || cartItemParam.getItemIds().length == 0)
			return null;
		List<CartitemModel> list = cartItemDao.listItemsByUserIdAndIds(cartItemParam);
		if (list != null && !list.isEmpty()) {
			String[] ids = new String[list.size()];
			for (int i = 0; i < list.size(); i++) {
				StringBuffer sb = new StringBuffer();
				CartitemModel item = list.get(i);
				sb.append(item.getProductId());
				sb.append("_");
				sb.append(item.getQty());
				if (item.getDiscountId() != null) {
					sb.append("_");
					sb.append(item.getDiscountId());
				}
				ids[i] = sb.toString();
			}
			return ids;
		}
		return null;
	}
}