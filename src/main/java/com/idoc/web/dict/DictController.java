package com.idoc.web.dict;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.util.JSONUtils;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.code.def.TableColumn;
import com.idoc.code.factory.CodeGenerateFactory;
import com.idoc.constant.CommonConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.pagination.PaginationInfo;
import com.idoc.dao.pagination.PaginationList;
import com.idoc.form.DictForm;
import com.idoc.form.ParamForm;
import com.idoc.model.CodeInfo;
import com.idoc.model.Dict;
import com.idoc.model.DictVersion;
import com.idoc.model.Param;
import com.idoc.model.ProductModel;
import com.idoc.service.code.CodeServiceImpl;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.dict.GenerateDictModelServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.util.DataTypeCheckUtil;
import com.idoc.util.ErrorMessageUtil;
import com.idoc.util.TimestampMorpher;
import com.netease.common.util.StringUtil;

@Controller
@RequestMapping("/dict")
public class DictController {

	@Autowired
	private DictServiceImpl dictServiceImpl;

	@Autowired
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;

	@Autowired
	private GenerateDictModelServiceImpl generateDictModelServiceImpl;

	@Autowired
	private CodeServiceImpl codeServiceImpl;

	@RequestMapping("/index.html")
	public String dictIndex(@RequestParam(value = "productId", required = false)String productId, Model model){
		if(!DataTypeCheckUtil.isNumber(productId)){
			model.addAttribute("exceptionMsg", "参数格式有误，请检查参数。");
			return "/datatypeError";
		}
		ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));

		model.addAttribute("productId", productId);
		if(productModel != null){
			model.addAttribute("productName", productModel.getProductName());
		}
		return "/dict/dict";
	}
	@RequestMapping("checkDict.html")
	public String checkDict(String dictId, String productId, Model model){
		model.addAttribute("dictId", dictId);
		model.addAttribute("productId", productId);

		ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));

		if(productModel != null){
			model.addAttribute("productName", productModel.getProductName());
		}
		return "/dict/checkDict";
	}
	@RequestMapping("editDict.html")
	public String editDict(String dictId, String productId, Model model){
		model.addAttribute("dictId", dictId);
		model.addAttribute("productId", productId);

		ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));

		if(productModel != null){
			model.addAttribute("productName", productModel.getProductName());
		}
		return "/dict/editDict";
	}
	@RequestMapping("/queryDicts.html")
	@ResponseBody
	public Map<String, Object> queryDicts(@RequestParam("productId")String productId, @RequestParam(value="dictName")String dictName,
			@RequestParam("pageNum")Integer pageNum, Integer perPage){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, String> paraMap = new HashMap<String, String>();
		LogConstant.runLog.info("[DictController->queryResultDetail]productId is:" + productId + ", pageNum is:" + pageNum
					+", per page is:" + perPage);
		if(StringUtils.isEmpty(productId) || pageNum==null || perPage == null){
			retMap.put("retCode", "-1");
			return retMap;
		}
		paraMap.put("productId", productId);
		paraMap.put("dictName", dictName);
		PaginationInfo paginationInfo = new PaginationInfo(pageNum, perPage);
		PaginationList<Dict> list = dictServiceImpl.queryDicts(paraMap, paginationInfo);
		if(list != null && list.size()>0){
			retMap.put("retCode", "200");
			retMap.put("retDesc", list);
			retMap.put("page", list.getPaginationInfo());
		}else{
			retMap.put("retCode", "-1");
		}
		return retMap;
	}
	@RequestMapping("/addDict.html")
	public String interLogDetail(Model model, @RequestParam("productId")String productId){
		model.addAttribute("productId", productId);

		ProductModel productModel = productAndProjectServiceImpl.queryProductModelByProductId(Long.valueOf(productId));

		if(productModel != null){
			model.addAttribute("productName", productModel.getProductName());
		}
		return "/dict/addDict";
	}
	@RequestMapping("/queryDict.html")
	@ResponseBody
	public Map<String, Object> queryDict(String dictId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("dictId", dictId);
		Dict dict = dictServiceImpl.queryDict(paramMap);
		if(dict != null){
			retMap.put("retCode", 200);
			retMap.put("retContent", dict);
		}else{
			retMap.put("retCode", -1);
		}
		return retMap;
	}
	@RequestMapping("/importDict.html")
	@ResponseBody
	public Map<String, Object> importDict(@RequestParam("productId")String productId,
			@RequestParam("databaseDriver")String databaseDriver,
			@RequestParam("databaseUrl")String databaseUrl,
			@RequestParam("userName")String userName,
			@RequestParam("password")String password,
			@RequestParam("databaseName")String databaseName,
			@RequestParam("params")String params){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> paraMap = new HashMap<String, Object>();
		paraMap.put("databaseDriver", databaseDriver);
		paraMap.put("databaseUrl", databaseUrl);
		paraMap.put("userName", userName);
		paraMap.put("password", password);
		paraMap.put("databaseName", databaseName);
		String tables[] = params.split(",");
		for(String table : tables){
			String[] tableParams = table.split(":");
			String tableName = tableParams[0];//表名称
			String className = tableParams[1];//字典名称
			String codeName = tableParams[2];//字典描述
			paraMap.put("tableName", tableName);
			List<TableColumn> list = codeServiceImpl.getTableColumns(paraMap);
			ParamForm[] paramList = new ParamForm[list.size()];
			for(int i=0;i<list.size();i++){
				TableColumn colum = list.get(i);
				ParamForm param = new ParamForm();
				param.setParamName(colum.getField().toLowerCase());
				param.setParamType(getParamType(colum.getType()));
				param.setIsNecessary(1);
				paramList[i] = param;
			}
			DictForm dictForm = new DictForm();
			dictForm.setDictName(className);
			dictForm.setDictDesc(codeName);
			dictForm.setParams(paramList);
			dictForm.setProductId(Long.parseLong(productId));
			retMap = addOneDict(dictForm);
			if((int)(retMap.get("retCode")) != 200)
				return retMap;
		}
		return retMap;
	}
	private String getParamType(String type) {
		String paramType;
		if(type.contains("bigint")){
			paramType = "long";
		}else if(type.contains("tinyint") || type.contains("int")){
			paramType = "int";
		}else if(type.contains("varchar") || type.contains("text")){
			paramType = "string";
		}else{
			paramType = type;
		}
		return paramType;
	}
	@RequestMapping("/addDicts.html")
	@ResponseBody
	public Map<String, Object> addDicts(HttpServletRequest request){
		Map<String,Object> retMap = new HashMap<String,Object>();
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(request.getInputStream(), "UTF-8"));
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = in.readLine()) != null) {
				sb.append(line);
			}
			LogConstant.runLog.info(sb.toString());
			String s = sb.toString();
			JSONObject json = JSONObject.fromObject(s);
			DictForm dictForm = (DictForm) JSONObject.toBean(json, DictForm.class);
			retMap = addOneDict(dictForm);
		} catch (Exception e) {
			LogConstant.runLog.error(e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
		}

		return retMap;
	}

	public Map<String, Object> addOneDict(DictForm dictForm){
		Map<String,Object> retMap = new HashMap<String,Object>();
		String dictName = dictForm.getDictName();
		Long productId = dictForm.getProductId();
		Long dictId = dictForm.getDictId();
		Map<String, String> map = new HashMap<String, String>();
		map.put("dict_name", dictName.toLowerCase());
		map.put("product_id", String.valueOf(productId));
		if(dictId != null){
			map.put("dict_id", String.valueOf(dictId));
		}
		Dict existDict = dictServiceImpl.queryDictByName(map);
		if(existDict != null){
			retMap.put("retCode", 201);
			retMap.put("retDesc", "该字典名称已存在！");
		}else{
			Dict dict = dictServiceImpl.saveDictForm(dictForm);
			if(dict!=null && dict.getDictId()!=null){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "处理成功");
			}else{
				retMap.put("retCode", 500);
				retMap.put("retDesc", "内部错误");
			}
		}
		return retMap;
	}

	@RequestMapping("edit.html")
	@ResponseBody
	public Map<String,Object> editDict(String dictId) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		if(StringUtils.isBlank(dictId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Long id = Long.valueOf(dictId);
			Dict dict = dictServiceImpl.getDict(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("dict", dict);
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
				// 返回插入失败原因
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}

	@RequestMapping("save.html")
	@ResponseBody
	public Map<String,Object> saveDict(DictForm dictForm) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		if(dictForm == null){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Dict dict = dictServiceImpl.saveDictForm(dictForm);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
				retMap.put("dict", dict);
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
				// 返回插入失败原因
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}

	@RequestMapping("delete.html")
	@ResponseBody
	public Map<String,Object> deleteDict(String dictId) {

		Map<String,Object> retMap = new HashMap<String,Object>();

		if(StringUtils.isBlank(dictId)){
			retMap.put("retCode", 401);
			retMap.put("retDesc", "参数错误");
			return retMap;
		}
		try {
			Long id = Long.valueOf(dictId);
			dictServiceImpl.removeDict(id);
			String error = ErrorMessageUtil.getErrorMessages();
			if(StringUtils.isBlank(error)){
				retMap.put("retCode", 200);
				retMap.put("retDesc", "操作成功");
			}else{
				retMap.put("retCode", 402);
				retMap.put("retDesc", error);
				// 返回插入失败原因
			}
		} catch (Exception e) {
			LogConstant.runLog.error("异常错误",e);
			retMap.put("retCode", 500);
			retMap.put("retDesc", "内部错误");
			return retMap;
		}
		return retMap;
	}

	@RequestMapping("queryDictsByProduct.html")
	@ResponseBody
	public Map<String, Object> queryDictsByProduct(@RequestParam("productId")String productId){
		Map<String, Object> ret = new HashMap<String, Object>();
		if(StringUtils.isBlank(productId)){
			ret.put("retCode", 401);
			ret.put("retDesc", "参数错误");

			return ret;
		}

		List<Dict> dictList = dictServiceImpl.queryDictsByProduct(Long.valueOf(productId));
		if(CollectionUtils.isNotEmpty(dictList)){
			ret.put("dictList", dictList);
		}

		ret.put("retCode", 200);
		return ret;
	}
	/*@RequestMapping("validateName")
	@ResponseBody
	private Map<String, Object> validateDictName(@RequestParam("productId")String productId,
				@RequestParam("dictName")String dictName){
		Map<String, Object> retMap = new HashMap<String, Object>();
		//校验字典名称是否存在
		Map<String, String> param = new HashMap<String, String>();
		param.put("dict_name", dictName.toLowerCase());
		param.put("product_id", productId);
		Dict existDict = dictServiceImpl.queryDictByName(param);
		if(existDict != null){
			retMap.put("retCode", 200);
		}else{
			retMap.put("retCode", -1);
		}
		return retMap;
	}*/
	@RequestMapping("historyDict")
	private String dictHistory(@RequestParam("dictId")String dictId, @RequestParam("productId")String productId,
			@RequestParam("dictName")String dictName,@RequestParam("productName")String productName, Model model){

		model.addAttribute("dictName", dictName);
		model.addAttribute("dictId", dictId);
		model.addAttribute("productId", productId);
		model.addAttribute("productName", productName);

		return "/dict/dictHistory";
	}

	@RequestMapping("queryDictHistory")
	@ResponseBody
	private Map<String, Object> queryDictHistory(@RequestParam("dictId")String dictId,
			@RequestParam("pageNum")Integer pageNum, Integer perPage){
		Map<String, Object> retMap = new HashMap<String, Object>();
		if(StringUtils.isEmpty(dictId) || pageNum==null || perPage==null){
			retMap.put("retCode", "-1");
			return retMap;
		}
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("dictId", dictId);
		PaginationInfo paginationInfo = new PaginationInfo(pageNum, perPage);
		PaginationList<Dict> list = dictServiceImpl.queryDictHistory(paraMap, paginationInfo);
		if(list != null && list.size()>0){
			retMap.put("retCode", "200");
			retMap.put("retDesc", list);
			retMap.put("page", list.getPaginationInfo());
		}else{
			retMap.put("retCode", "-1");
		}
		return retMap;
	}
	@RequestMapping("checkDictHistory")
	private String checkDictHistory(@RequestParam("dictId")String dictId, @RequestParam("version")String version,
			@RequestParam("productId")String productId, @RequestParam("productName")String productName, Model model){

		model.addAttribute("dictId", dictId);
		model.addAttribute("version", version);
		model.addAttribute("productId", productId);
		model.addAttribute("productName", productName);
		return "/dict/checkDictHistory";
	}
	@RequestMapping("queryDictVersionById")
	@ResponseBody
	private Map<String, Object> queryDictVersion(@RequestParam("dictId")String dictId, @RequestParam("version")String version){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, String> paraMap = new HashMap<String, String>();
		paraMap.put("dictId", dictId);
		paraMap.put("version", version);
		DictVersion dictVersion = dictServiceImpl.queryDictVersion(paraMap);
		if(dictVersion != null){
			String snapShot = dictVersion.getSnapshot();
			JSONObject snapShotJson = JSONObject.fromObject(snapShot);
			String[] jsonFormats = {CommonConstant.DATE_FORMAT_PATTERN};
			JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(jsonFormats));
			Dict dict = (Dict) JSONObject.toBean(snapShotJson, Dict.class);
			convertJsonToParamList(snapShotJson, dict, new ArrayList<Param>(), new HashSet<String>());
			retMap.put("retCode", 200);
			retMap.put("dict", dict);
		}
		return retMap;
	}
	@RequestMapping("revertDictVersion")
	@ResponseBody
	private Map<String, Object> revertDictVersion(@RequestParam("dictId")String dictId, @RequestParam("version")String version){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> dictMap = queryDictVersion(dictId, version);
		Dict dict = (Dict) dictMap.get("dict");
		if(dict == null){
			retMap.put("retCode", "-1");
			return retMap;
		}

		return retMap;
	}
	private void convertJsonToParamList(JSONObject json, Dict dict, List<Param> list, HashSet<String> set){
		JSONArray array = json.getJSONArray("params");
		String[] jsonFormats = {CommonConstant.DATE_FORMAT_PATTERN};
		JSONUtils.getMorpherRegistry().registerMorpher(new TimestampMorpher(jsonFormats));
		if(array == null){
			return;
		}
		int len = array.size();
		for(int i=0; i<len; i++){
			JSONObject jo = (JSONObject) array.get(i);
			Param param = (Param) JSONObject.toBean(jo, Param.class);
			String dictStr = jo.getString("dict");
			if(StringUtils.isNotEmpty(dictStr) && !"null".equals(dictStr)){
				JSONObject dictJson = JSONObject.fromObject(dictStr);
				String name = dictJson.getString("dictName");
				if(set.contains(name)){
					continue;
				}else{
					List<Param> subParamList = new ArrayList<Param>();
					set.add(name);
					Dict subDict = (Dict) JSONObject.toBean(dictJson, Dict.class);
					convertJsonToParamList(dictJson, subDict, subParamList, set);
					param.setDictId(subDict.getDictId());
					param.setDict(subDict);
				}
			}
			list.add(param);
		}
		dict.setParams(list);
	}
	/**
	 * 字典复制,复制得到的新字典与原有字典不存在依赖关系
	 */
	@RequestMapping("copyDict")
	@ResponseBody
	private Map<String, Object> copyDict(@RequestParam("dictId")String dictId, @RequestParam("productId")String productId,
			@RequestParam("newDictName")String newDictName, @RequestParam("newDictDesc")String newDictDesc){
		Map<String, Object> map = new HashMap<String, Object>();
		Dict oldDict = dictServiceImpl.getDict(Long.parseLong(dictId));
		if(oldDict == null){
			map.put("retCode", -1);
			map.put("retDesc", "复制字典未查询到字典源！");
			LogConstant.debugLog.info("复制字典未查询到字典源！");
			return map;
		}
		if(org.apache.commons.lang3.StringUtils.isEmpty(newDictName)){
			LogConstant.debugLog.info("字典名称不能为空！");
			map.put("retCode", "-2");
			map.put("retDesc", "字典名称不能为空！");
			return map;
		}
		if(org.apache.commons.lang3.StringUtils.isEmpty(productId)){
			LogConstant.debugLog.info("产品id不能为空！");
			map.put("retCode", "-3");
			map.put("retDesc", "产品id不能为空！");
			return map;
		}
		//校验字典名称是否存在
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("dict_name", newDictName);
		paramMap.put("product_id", productId);
		Dict existDict = dictServiceImpl.queryDictByName(paramMap);
		if(existDict != null){
			map.put("retCode", -4);
			map.put("retDesc", "该字典名称已存在，请重新填写");
			return map;
		}
		//设置新字典的名称、描述等信息
		oldDict.setDictName(newDictName);
		oldDict.setDictDesc(newDictDesc);
		oldDict.setDictId(null);
		oldDict.setCreateTime(new Timestamp(new Date().getTime()));
		oldDict.setProductId(Long.parseLong(productId));
		//设置param的paramId为null，重新插入一份新的param
		List<Param> params = oldDict.getParams();
		if(params != null){
			for(Param param : params){
				param.setParamId(null);
			}
		}
		Dict newDict = dictServiceImpl.copyDict(oldDict);
		if(newDict.getDictId() != null){
			map.put("retCode", 200);
			map.put("retDesc", "字典复制成功！");
		}else{
			map.put("retCode", -5);
			map.put("retDesc", "字典复制失败，请联系相关人员！");
		}
		return map;
	}

	@RequestMapping("generateDictModel.html")
	public ResponseEntity<byte[]> generateDictModel(
			@RequestParam("dictId") String dictId,
			@RequestParam("dictName") String dictName) throws IOException {
		if(StringUtil.isEmpty(dictId) || StringUtil.isEmpty(dictName)){
			return new ResponseEntity<byte[]>("请求的参数为空，生成字典model代码失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		byte[] flieByteStream = null;

		flieByteStream = generateDictModelServiceImpl.generateDictModel(dictId);
		if(flieByteStream == null){
			return new ResponseEntity<byte[]>("后台生成字典model代码失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
		}
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = dictName + "Model_" + sdf.format(new Date()) + ".java";
        String fileName = new String(name.getBytes("UTF-8"),"iso-8859-1");
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(flieByteStream, headers, HttpStatus.CREATED);
	}
	@RequestMapping("generateDictClass.html")
	@ResponseBody
	public Map<String,Object> generateDictClass(
			@RequestParam("dictId") String dictId,
			@RequestParam("dictName") String dictName) throws IOException {
		Map<String, Object> retMap = new HashMap<String, Object>();
		List<CodeInfo> sourceCodeInfo = null;
		try {
			sourceCodeInfo = generateDictModelServiceImpl.generateDictClass(dictId);
		} catch(Exception e) {
			e.printStackTrace();
			retMap.put("retCode", 401);
			return retMap;
		}

		if(sourceCodeInfo != null && sourceCodeInfo.size() > 0){
			retMap.put("retCode", 200);
			retMap.put("sourceCodeInfo", sourceCodeInfo);
		}else{
			retMap.put("retCode", 400);
		}

		return retMap;
	}
}