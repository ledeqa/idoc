package com.idoc.web.onlineInterface;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.idoc.constant.FileUploadConstant;
import com.idoc.constant.LogConstant;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.form.DictForm;
import com.idoc.form.ParamForm;
import com.idoc.model.Dict;
import com.idoc.model.InterfaceOnline;
import com.idoc.model.Param;
import com.idoc.model.UserModel;
import com.idoc.service.dict.DictServiceImpl;
import com.idoc.service.interfaceOnline.InterfaceOnlineService;
import com.netease.common.util.JsonUtil;

@Controller
@RequestMapping("/idoc/import")
public class ImportDocsController {

	@Autowired
	private DictServiceImpl dictServiceImpl;
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	@Autowired
	private InterfaceOnlineService interfaceOnlineService;
	private static final String[] PARAM_TYPE = {"int", "Int", "string", "String", 
		"boolean", "Boolean", "object", "Object"};
	//上传接口doc文档，进行处理
	@RequestMapping("/dicts")
	public void uploadDictDocument(String callback, @RequestParam("file") MultipartFile file, 
			@RequestParam("productId")String productId, 
			HttpServletRequest request, HttpServletResponse response) {

		Map<String,Object> ret = new HashMap<String,Object>();
		StringBuffer errorMsgBuilder = new StringBuffer();
		String fileName = file.getOriginalFilename();
		try {
			handleWord(productId, null, file.getInputStream(), errorMsgBuilder, false);
		} catch (IOException e) {
			LogConstant.debugLog.error("Fail to upload document " + fileName + ", inner error", e);
			ret.put("retCode", FileUploadConstant.SYSTEM_ERROR);
			ret.put("errorContent", errorMsgBuilder.toString());
		}
		ret.put("errorContent", errorMsgBuilder.toString());
		if(StringUtils.isEmpty(errorMsgBuilder.toString())){
			ret.put("retCode", 200);
		}else{
			ret.put("retCode", -1);
		}
		writeResponseMessage(response, ret, callback);
	}
	@RequestMapping("/apis")
	public void uploadApiDocument(String callback, @RequestParam("file") MultipartFile file, 
			@RequestParam("productId")String productId, 
			@RequestParam(value="onlinePageId", required=false)String onlinePageId,
			HttpServletRequest request, HttpServletResponse response) {
		
		Map<String,Object> ret = new HashMap<String,Object>();
		StringBuffer errorMsgBuilder = new StringBuffer();
		String fileName = file.getOriginalFilename();
		try {
			handleWord(productId, onlinePageId, file.getInputStream(), errorMsgBuilder, true);
		} catch (IOException e) {
			LogConstant.debugLog.error("Fail to upload document " + fileName + ", inner error", e);
			ret.put("retCode", FileUploadConstant.SYSTEM_ERROR);
			ret.put("errorContent", errorMsgBuilder.toString());
			writeResponseMessage(response, ret, callback);
			return;
		}
		ret.put("errorContent", errorMsgBuilder.toString());
		if(StringUtils.isEmpty(errorMsgBuilder.toString())){
			ret.put("retCode", 200);
		}else{
			ret.put("retCode", -1);
		}
		writeResponseMessage(response, ret, callback);
	}
	//处理word文档内容，注意字典和接口需要分别处理
	private void handleWord(String productId, String onlinePageId,
			InputStream inputStream, StringBuffer builder, boolean isApi) {
		FileInputStream fis = null;
		XWPFDocument document = null;
		try {
			document = new XWPFDocument(inputStream);
			List<XWPFTable> tables = document.getTables();
			if(!isApi){
				for(XWPFTable table : tables){//获取表格
					handleDictInfos(table, productId, builder);
				}
			}else{
				handleApiInfos(onlinePageId, productId, tables.get(0), tables.get(1), tables.get(2), builder);
			}
		} catch (Exception e) {
			LogConstant.debugLog.error("handle word document failed, " + e.getMessage());
			return;
		}finally{
			try {
				if(document != null){
					document.close();
				}
				if(fis != null){
					fis.close();
				}
			} catch (IOException e) {
				LogConstant.debugLog.error("handle word document failed, " + e.getMessage());
				return;
			}
		}
		return;
	}
	//响应信息
	private void writeResponseMessage(HttpServletResponse response, Map<String,Object> ret, String callback) {
		response.addHeader("content-type", "text/html");
		response.setCharacterEncoding("UTF-8");
		String json = JsonUtil.mapToJson(ret);
		try {
			if(callback != null && !callback.equals("")){
				response.getWriter().write("<script>window.top." + callback + "('" + json + "');</script>");
			}else{
				response.getWriter().write(json);
			}
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//处理字典信息
	private void handleDictInfos(XWPFTable dictTable, String productId, StringBuffer builder){
		if(dictTable == null){
			return;
		}
		List<XWPFTableRow> rows = dictTable.getRows();
		if(rows == null || rows.size() == 0){
			return;
		}
		XWPFTableRow dictNameRow = rows.get(0);
		XWPFTableCell dictNameCell = dictNameRow.getCell(1);
		String dictName = dictNameCell.getText();
		if(StringUtils.isNotEmpty(dictName)){
			dictName = dictName.trim();
		}
		Dict dict = new Dict();
		dict.setDictName(dictName);
		//查询当前字典是否已经存在
		Map<String, String> map = new HashMap<String, String>();
		map.put("dict_name", dictName);
		map.put("product_id", productId);
		Dict existDict = dictServiceImpl.queryDictByName(map);
		if(existDict != null){//如果字典存在，则不在进行下一步操作
			builder.append(dictName).append("字典已存在，新增失败").append("\n");
			return;
		}
		List<ParamForm> paramList = new ArrayList<ParamForm>();
		for(int i=0; i<rows.size(); i++){//第一行为表头，不做处理
			if(i < 2){
				continue;
			}
			XWPFTableRow row = rows.get(i);
			List<XWPFTableCell> cells = row.getTableCells();
			if(cells == null){
				continue;
			}
			ParamForm paramForm = new ParamForm();
			for(int j=0; j<cells.size(); j++){
				XWPFTableCell cell = cells.get(j);
				String cellText = cell.getText();
				switch(j){
					case 0://设置参数名称
						paramForm.setParamName(cellText);
						break;
					case 1://设置参数类型
						if(cellText.contains("List<")){//list转化为数组格式
							cellText = cellText.replace("List<", "array<");
						}
						if(cellText.contains("list<")){
							cellText = cellText.replace("list<", "array<");
						}
						paramForm.setParamType(cellText);
						paramForm.setParamType(cellText);
						cellText = cellText.replace("array<", "");
						cellText = cellText.replace(">", "");
						if(!Arrays.asList(PARAM_TYPE).contains(cellText)){
							Map<String, String> paramMap = new HashMap<String, String>();
							paramMap.put("dict_name", cellText);
							paramMap.put("product_id", productId);
							Dict dictExist = dictServiceImpl.queryDictByName(map);
							if(dictExist != null){
								paramForm.setDictId(dict.getDictId());
							}
						}
						break;
					case 2://设置参数描述
						paramForm.setParamDesc(cellText);
						break;
					case 3://设置参数的备注数据
						paramForm.setRemark(cellText);
						break;
					case 4://设置参数是否必需
						if("Y".equals(cellText) || "是".equals(cellText)){
							paramForm.setIsNecessary(1);
						}else{
							paramForm.setIsNecessary(0);
						}
						break;
					default:
						break;
				}
			}
			paramList.add(paramForm);		
		}
		DictForm insertForm = new DictForm();
		int len = paramList.size();
		ParamForm[] forms = new ParamForm[len];
		for(int i=0; i<len; i++){
			forms[i] = paramList.get(i);
		}
		insertForm.setDictName(dictName);
		insertForm.setParams(forms);
		insertForm.setProductId(Long.parseLong(productId));
		Dict insertDict = dictServiceImpl.saveDictForm(insertForm);
		if(insertDict == null){
			builder.append(dictName).append("新增失败，请联系管理员！").append("\n");
		}
		return;
	}
	//处理接口信息
	private void handleApiInfos(String onlinePageId, String productId, 
			XWPFTable creatorTable, XWPFTable requestTable, XWPFTable responseTable, StringBuffer buffer){
		InterfaceOnline onlineApi = new InterfaceOnline();
		//处理接口用户、url及描述相关信息
		List<XWPFTableRow> apiInfoRows = creatorTable.getRows();
		if(apiInfoRows == null || apiInfoRows.size() == 0){
			buffer.append("无法获取接口创建者、url等相关信息！").append("\n");
			return;
		}
		UserModel creator = null;
		/*每一行有两列，第一列是参数名，如责任人，第二列是参数值，如张三
		第一行是责任人*/
		for(int i=0; i<apiInfoRows.size(); i++){
			XWPFTableRow row = apiInfoRows.get(i);
			XWPFTableCell paramNameCell = row.getCell(0);
			XWPFTableCell paramValueCell = row.getCell(1);
			String nameText = paramNameCell.getText();
			if(StringUtils.isNotEmpty(nameText)){
				nameText = nameText.trim();
			}
			String valueText = paramValueCell.getText();
			if(StringUtils.isNotEmpty(valueText)){
				valueText = valueText.trim();
			}
			switch(i){
				case 0://第一行是接口名称信息
					onlineApi.setInterfaceName(valueText);
					break;
				case 1://第一行是责任人信息
					creator = loginDaoImpl.queryUserModelByUserName(valueText);
					if(creator != null){
						onlineApi.setCreator(creator);
						onlineApi.setCreatorId(creator.getUserId());
					}else{
						buffer.append("无法获取接口创建者信息！").append("\n");
						return;
					}
					break;
				case 2://第二行是接口url信息
					onlineApi.setUrl(valueText);
					break;
				case 3://第三行是接口的功能描述信息
					onlineApi.setDesc(valueText);
					break;
				case 4://第四行是接口的请求类型
					if("get".equalsIgnoreCase(valueText)){
						onlineApi.setRequestType(1);
					}else if("post".equalsIgnoreCase(valueText)){
						onlineApi.setRequestType(2);
					}else if("put".equalsIgnoreCase(valueText)){
						onlineApi.setRequestType(3);
					}else{
						onlineApi.setRequestType(4);
					}
					break;
				default:
					break;
			}		
		}
		//处理请求参数内容
		List<XWPFTableRow> reqParamRows = requestTable.getRows();
		List<Param> reqParams = new ArrayList<Param>();
		if(reqParamRows == null || reqParamRows.size() == 0){
			onlineApi.setReqParams(reqParams);
		}
		for(int j=2; j<reqParamRows.size(); j++){
			XWPFTableRow row = reqParamRows.get(j);
			Param param = new Param();
			param.setStatus(1);
			List<XWPFTableCell> cells = row.getTableCells();
			for(int i=0; i<cells.size(); i++){
				XWPFTableCell cell = cells.get(i);
				String cellText = cell.getText();
				switch(i){
					case 0://设置参数名称
						param.setParamName(cellText);
						break;
					case 1://设置参数类型
						if(cellText.contains("List<")){//list转化为数组格式
							cellText = cellText.replace("List<", "array<");
						}
						if(cellText.contains("list<")){
							cellText = cellText.replace("list<", "array<");
						}
						if("int".equalsIgnoreCase(cellText)){
							cellText = "number";
						}
						param.setParamType(cellText);
						cellText = cellText.replace("array<", "");
						cellText = cellText.replace(">", "");
						if(!Arrays.asList(PARAM_TYPE).contains(cellText)){
							Map<String, String> map = new HashMap<String, String>();
							map.put("dict_name", cellText);
							map.put("product_id", productId);
							Dict dict = dictServiceImpl.queryDictByName(map);
							if(dict != null){
								param.setDictId(dict.getDictId());
							}
						}
						break;
					case 2://设置参数描述
						param.setParamDesc(cellText);
						break;
					case 3://设置参数的备注消息
						param.setRemark(cellText);
						break;
					case 4://设置参数是否必需
						if("Y".equals(cellText) || "是".equals(cellText)){
							param.setIsNecessary(1);
						}else{
							param.setIsNecessary(0);
						}
						break;
					default:
						break;
				}
			}
			reqParams.add(param);
		}
		onlineApi.setReqParams(reqParams);
		//处理响应参数内容
		List<XWPFTableRow> retParamRows = responseTable.getRows();
		List<Param> retParams = new ArrayList<Param>();
		if(retParamRows == null || retParamRows.size() == 0){
			onlineApi.setRetParams(reqParams);
		}
		for(int j=2; j<retParamRows.size(); j++){
			XWPFTableRow row = retParamRows.get(j);
			Param param = new Param();
			param.setStatus(1);
			List<XWPFTableCell> cells = row.getTableCells();
			for(int i=0; i<cells.size(); i++){
				XWPFTableCell cell = cells.get(i);
				String cellText = cell.getText();
				switch(i){
					case 0://设置参数名称
						param.setParamName(cellText);
						break;
					case 1://设置参数类型
						if(cellText.contains("List<")){//list转化为数组格式
							cellText = cellText.replace("List<", "array<");
						}
						if(cellText.contains("list<")){
							cellText = cellText.replace("list<", "array<");
						}
						param.setParamType(cellText);
						cellText = cellText.replace("array<", "");
						cellText = cellText.replace(">", "");
						if(!Arrays.asList(PARAM_TYPE).contains(cellText)){
							Map<String, String> map = new HashMap<String, String>();
							map.put("dict_name", cellText);
							map.put("product_id", productId);
							Dict dict = dictServiceImpl.queryDictByName(map);
							if(dict != null){
								param.setDictId(dict.getDictId());
							}
						}
						break;
					case 2://设置参数描述
						param.setParamDesc(cellText);
						break;
					case 3://设置参数的备注数据
						param.setRemark(cellText);
						break;
					case 4://设置参数是否必需
						if("Y".equals(cellText) || "是".equals(cellText)){
							param.setIsNecessary(1);
						}else{
							param.setIsNecessary(0);
						}
						break;
					default:
						break;
				}
			}
			retParams.add(param);
		}
		onlineApi.setRetParams(retParams);
		onlineApi.setProductId(Long.parseLong(productId));
		onlineApi.setOnlinePageId(Long.parseLong(onlinePageId));
		onlineApi.setInterfaceType(1);//默认接口类型为ajax
		onlineApi.setIsNeedInterfaceTest(1);//默认需要进行测试
		onlineApi.setIsNeedPressureTest(0);//默认不需要进行压力测试
		onlineApi.setInterfaceStatus(9);//状态为已上线
		onlineApi.setOnlineVersion(1l);//版本为1
		onlineApi.setStatus(1);//1为有效状态
		InterfaceOnline online = interfaceOnlineService.saveApiDocInterface(onlineApi);
		if(online.getInterfaceId() == null){
			buffer.append("接口新增失败，请联系相关人员进行查看！").append("\n");
		}
		return;
	}
}
