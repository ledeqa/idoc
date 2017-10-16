package com.idoc.web.statistics;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.dao.statistics.StatisticsProductDaoImpl;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.LoginUserInfo;
import com.idoc.model.StatisticsProductModel;
import com.idoc.model.login.ProductListModel;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.web.BaseController;

@Controller
public class ProductCompareController extends BaseController {
	@Autowired
	private MemcacheServer memcacheServer;
	
	@Autowired
	private LoginIndexServiceImpl loginIndexServiceImpl;
	
	@Autowired
	private StatisticsProductDaoImpl statisticsProductDaoImpl;
	
	@ModelAttribute(value = "productList")
	public List<ProductListModel> getProductList(HttpServletRequest request) throws IOException{
		String englishName = null;
		List<ProductListModel> productList = null;
		LoginUserInfo userInfo = (LoginUserInfo) memcacheServer.getCacheData(request.getSession().getId());
		if(userInfo != null){
			englishName = userInfo.getEnglishName();
		}
		if(StringUtils.isEmpty(englishName)){
			return productList;
		}
		int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
		productList = loginIndexServiceImpl.queryProductModelByPage(adminFlag, englishName, null);
		
		return productList;
	}
	
	@RequestMapping("/idoc/statistics/productCompare.html")
	public String productCompare(){	
		return "/statistics/productCompare";
	}
	
	@RequestMapping("/idoc/statistics/getSelectedStatisticsProduct.html")
	@ResponseBody
	public Map<String,Object> getSelectedStatisticsProduct(
			@RequestParam("products")String products, 
			@RequestParam("startTime")String startTime,
			@RequestParam("endTime")String endTime) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		String[] productIds = products.split(",");
		map.put("statisticsProductList", Arrays.asList(productIds));
		map.put("startTime", startTime);
		map.put("endTime", endTime);
		List<StatisticsProductModel> statisticsProductList = statisticsProductDaoImpl.selectStatisticsProductByProductIds(map);
		if(statisticsProductList != null){
			retMap.put("retCode", 200);
			retMap.put("statisticsProductList", statisticsProductList);
		}else{
			retMap.put("retCode", 401);
			retMap.put("retDesc", "获取产品statisticsProductModel错误");
			return retMap;
		}
		return retMap;
	}
}