package com.idoc.web.login;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.LogConstant;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.memcache.MemcacheServer;
import com.idoc.model.LoginUserInfo;
import com.idoc.model.UserModel;
import com.idoc.model.login.ProductListModel;
import com.idoc.model.login.SearchInterfaceInProduct;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.shiro.token.TokenManager;
import com.idoc.util.CountSession;
import com.idoc.util.LoggerUtils;
import com.idoc.util.MD5Util2;
import com.idoc.util.VerifyCodeUtils;

import net.sf.json.JSONObject;

@Controller
public class LoginController {

	@Autowired
	private MemcacheServer memcacheServer;

	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;

	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpServletResponse response) {
		return "login/login";
	}
	
	@RequestMapping("/submitLogin")
	@ResponseBody
	public Map<String, Object> submigLogin(UserModel entity, Boolean rememberMe, HttpServletRequest request){
		Map<String, Object> retMap = new HashMap<>();
		try {
			entity = TokenManager.login(entity,rememberMe);
			retMap.put("status", 200);
			retMap.put("message", "登录成功");
			
			LoginUserInfo userInfo = new LoginUserInfo();
			userInfo.setUserName(entity.getNickName());
			userInfo.setEnglishName(entity.getUserName());
			userInfo.setEmail(entity.getCorpMail());
			String sessionId = request.getSession().getId();
			System.out.println("session id : " + sessionId);
			memcacheServer.setCacheData(sessionId, userInfo, 12 * 60 * 60 * 1000); // 设置用户信息保存memcache中有效期为 12小时 12 * 60 * 60 * 1000
			
			if(loginIndexServiceImpl.confirmAdmin(entity.getUserName()) == 1){
				request.getSession().setAttribute("admin", true);
			}
			LogConstant.runLog.info("fullName=" + entity.getNickName());
			
			SavedRequest savedRequest = WebUtils.getSavedRequest(request);
			String url = null ;
			if(null != savedRequest){
				url = savedRequest.getRequestUrl();
			}
			LoggerUtils.fmtDebug(getClass(), "获取登录之前的URL:[%s]",url);
			//如果登录之前没有地址，那么就跳转到首页。
			if(StringUtils.isBlank(url)){
				url = request.getContextPath() + "/idoc/index.html";
			}
			//跳转地址
			retMap.put("back_url", url);
		/**
		 * 这里其实可以直接catch Exception，然后抛出 message即可，但是最好还是各种明细catch 好点。。
		 */
		} catch (DisabledAccountException e) {
			retMap.put("status", 500);
			retMap.put("message", "帐号已经禁用。");
		} catch (Exception e) {
			retMap.put("status", 500);
			retMap.put("message", "帐号或密码错误");
		}
		return retMap;
	}
	
	@RequestMapping("/register")
	public String register(HttpServletRequest request, HttpServletResponse response) {
		return "/login/register";
	}
	
	@RequestMapping("/submitRegister")
	@ResponseBody
	public Map<String, Object> submitRegister(UserModel entity, String vcode){
		Map<String, Object> retMap = new HashMap<>();
		retMap.put("status", 400);
		if(!VerifyCodeUtils.verifyCode(vcode)){
			retMap.put("message", "验证码不正确！");
			return retMap;
		}
		String email =  entity.getCorpMail();
		
		List<UserModel> users = loginIndexServiceImpl.selectUserByCorpMail(email);
		if(null != users && !users.isEmpty()){
			retMap.put("message", "帐号|Email已经存在！");
			return retMap;
		}
		String userName = email;
		if(email.contains("@")) {
			userName = email.substring(0, email.indexOf("@"));
		}
		entity.setUserName(userName);
		Timestamp time = new Timestamp(System.currentTimeMillis());
		entity.setCreateTime(time);
		entity.setLastLoginTime(time);
		//把密码md5
		entity.setPswd(MD5Util2.MD5EncodeWithUtf8(entity.getCorpMail() + "#" + entity.getPswd()));
		//设置有效
		entity.setStatus(UserModel._1.intValue());
		
		loginIndexServiceImpl.register(entity);
		LoggerUtils.fmtDebug(getClass(), "注册插入完毕！", JSONObject.fromObject(entity).toString());
		entity = TokenManager.login(entity, Boolean.TRUE);
		LoggerUtils.fmtDebug(getClass(), "注册后，登录完毕！", JSONObject.fromObject(entity).toString());
		retMap.put("message", "注册成功！");
		retMap.put("status", 200);
		return retMap;
	}
	
	@RequestMapping("idoc/index")
	public String indexAfterSignIn(HttpServletRequest request, ModelMap model) throws IOException {
		System.out.println("session id : " + request.getSession().getId());
		LoginUserInfo userInfo = (LoginUserInfo) memcacheServer.getCacheData(request.getSession().getId());
		HttpSession session = request.getSession();
        String sessionId = session.getId();
        Cookie[] cookies = request.getCookies();
        if(session.getAttribute("firstRequest")==null){                  //只要访问idoc的url不管是否登录都会创建一个session，并且创建了cookie
        	CountSession.onlinePeople.getAndIncrement();             //当前登录总人数
//        	CountSession.todayPeople.getAndIncrement();              //当天使用总次数，登录就代表使用一次，需要一个线程在24点将此值设置为0
//        	CountSession.historyPeople.getAndIncrement();            //历史登录总次数，登录代表用了，在Session内登录，用过一次就记一次
        }
		for(Cookie cookie:cookies){
			if(cookie.getName().equals("JSESSIONID")){
			   String cookieValue = cookie.getValue();
			   if(cookieValue.equals(sessionId)){                     //cookie里取到了本次回话ID，证明不是首次访问，那么统计不加1
				   session.setAttribute("firstRequest", userInfo.getEmail());
			   }
			}
		}
		String englishName = null;

		if(userInfo != null){
			englishName = userInfo.getEnglishName();
		}
		int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
		model.put("adminFlag", adminFlag);
		model.put("englishName", englishName);
		return "login/myPage";
	}

	@RequestMapping("idoc/getMyProduct")
	@ResponseBody
	public Map<String, Object> getMyProduct(HttpServletRequest request) throws IOException {

		Map<String, Object> retMap = new HashMap<String, Object>();
		String englishName = null;
		LoginUserInfo userInfo = (LoginUserInfo) memcacheServer.getCacheData(request.getSession().getId());
		if(userInfo != null){
			englishName = userInfo.getEnglishName();
			retMap.put("userName", userInfo.getUserName());
		}
		//String userName = (String)request.getSession().getAttribute("userName");
		//String email = request.getParameter("openid.sreg.email");;
		//loginIndexServiceImpl.insertUser(userName,englishName,email);
		if(StringUtils.isEmpty(englishName)){
			retMap.put("retCode", -1);
			return retMap;
		}
		int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
		List<ProductListModel> productList = loginIndexServiceImpl.queryProductModelByPage(adminFlag, englishName, null);
		retMap.put("retContent", productList);
		retMap.put("retCode", 200);
		return retMap;
	}

	@RequestMapping("idoc/getMyProducts")
	@ResponseBody
	public Map<String, Object> getMyProducts(@RequestParam("adminFlag") int adminFlag,
			@RequestParam("englishName") String englishName,
			@RequestParam("pageNum") Integer pageNum,
			@RequestParam("perPage") Integer perPage){
		LogConstant.debugLog.info("adminFlag is: "+adminFlag+", perPage is: "+perPage);
		Map<String,Object> retMap = new HashMap<String,Object>();
		PaginationInfo paginationInfo = new PaginationInfo(pageNum, perPage);
		List<ProductListModel> productModels = loginIndexServiceImpl.queryProductModelByPage(adminFlag, englishName, paginationInfo);
		retMap.put("retCode", 200);
		retMap.put("retContent", productModels);
		retMap.put("page", paginationInfo);
		return retMap;
	}

	@RequestMapping("idoc/deleteProduct")
	@ResponseBody
	public Map<String,Object> deleteProduct(@RequestParam("productId") String productId,
		@RequestParam("productName") String productName

			) throws IOException {


		Map<String, Object> retMap = new HashMap<String, Object>();

		if(StringUtils.isBlank(productName)||StringUtils.isBlank(productId) ){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		if (!"Demo".equals(productName.trim())) {
			int ret = loginIndexServiceImpl.deleteProduct(productId);
			if (ret == 1) {
				retMap.put("retCode", 200);

			} else {
				retMap.put("retCode", -1);
			}
		}

		return retMap;
	}

	/**
	 * 根据接口名称或接口url查询产品下接口
	 * @param productId
	 * @param interfaceName
	 * @param interfaceUrl
	 * @return
	 * @throws IOException
	 */
	@RequestMapping("idoc/searchInterfaceId")
	@ResponseBody
	public Map<String,Object> searchInterfaceId(
			@RequestParam(value = "productId", required = true) String productId,
			@RequestParam(value = "interfaceName", required = false) String interfaceName,
			@RequestParam(value = "interfaceUrl", required = false) String interfaceUrl
			) throws IOException {
		Map<String, Object> retMap = new HashMap<String, Object>();

		if(StringUtils.isBlank(productId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数缺失");
			return retMap;
		}
		List<SearchInterfaceInProduct> list = loginIndexServiceImpl.searchInterfaceId(productId, interfaceName, interfaceUrl);
		if (list == null ||list.size() <= 0) {
			retMap.put("retCode", -1);
			retMap.put("retDesc", "没有查询到满足条件的接口！");
		} else {
			retMap.put("retCode", 200);
			retMap.put("retDesc", "查询成功！");
			retMap.put("interfaceIdList", list);
		}

		return retMap;
	}
}