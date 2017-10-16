package com.idoc.web.statistics;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.util.FileUtil;
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

import com.idoc.constant.LogConstant;
import com.idoc.dao.docManage.InterfaceStatusChangementDaoImpl;
import com.idoc.dao.productAndProject.ProductUserDaoImpl;
import com.idoc.dao.productAndProject.ProjectDaoImpl;
import com.idoc.model.Interface;
import com.idoc.model.InterfaceStatusChangement;
import com.idoc.model.Module;
import com.idoc.model.ProjectModel;
import com.idoc.model.UserModel;
import com.idoc.service.data.ExportInterInfo2ExcelServiceImpl;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.docManage.ModuleServiceImpl;
import com.idoc.web.BaseController;
import com.netease.common.util.StringUtil;

@Controller
public class ProjectStatisticsController extends BaseController {
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private InterfaceStatusChangementDaoImpl interfaceStatusChangementDaoImpl;
	
	@Autowired
	private ProjectDaoImpl projectDaoImpl;
	
	@Autowired
	private ProductUserDaoImpl productUserDaoImpl;
	
	@Autowired
	private ModuleServiceImpl moduleServiceImpl;
	
	@Autowired
	private ExportInterInfo2ExcelServiceImpl exportInterInfo2ExcelServiceImpl;
	
	@RequestMapping("/idoc/statistics/projectStatistics.html")
	public String projectStatistics(@RequestParam("projectId")Long projectId, Model model){	
		ProjectModel project = projectDaoImpl.selectProjectModelByProjectId(projectId);
		List<Module> moduleList = moduleServiceImpl.getModuleListByProjectId(projectId.toString());
		if(project != null){
			model.addAttribute("projectName", project.getProjectName());
		}if(moduleList!=null && moduleList.size()>0){
			model.addAttribute("moduleList", moduleList);
		}
		model.addAttribute("projectId", projectId);
		return "/statistics/projectStatistics";
	}
	
	@RequestMapping("/idoc/statistics/getAllInterfaceInProject.html")
	@ResponseBody
	public Map<String,Object> getAllInterfaceInProject(@RequestParam("projectId")Long projectId, @RequestParam("modules")String modules) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		List<Interface> interfaces = interfaceServiceImpl.getAllInterfaceListByProjectId(projectId,modules);
		if(interfaces != null && interfaces.size() > 0){
			retMap.put("retCode", 200);
			retMap.put("interfaces", interfaces);
		}else{
			retMap.put("retCode", 401);
			retMap.put("retDesc", "获取项目下接口错误");
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/statistics/getInterfaceForceBackReason.html")
	@ResponseBody
	public Map<String,Object> getInterfaceForceBackReason(@RequestParam("interfaceIdArray")String[] interfaceIdArray) {
		Map<String,Object> retMap = new HashMap<String,Object>();
		if(interfaceIdArray == null || interfaceIdArray.length == 0){
			retMap.put("retCode", 300);
			retMap.put("forceBackReason", null);
			retMap.put("retDesc", "参数为空");
			return retMap;
		}
		List<Long> interfaceIds = new ArrayList<Long>();
		for(String id : interfaceIdArray){
			try{
				interfaceIds.add(Long.parseLong(id));
			}catch(NumberFormatException e){
				LogConstant.debugLog.info("获取接口强制回收原因时，转换接口id异常：" + e.getMessage());
			}
			
		}
		List<InterfaceStatusChangement> forceBackReason = interfaceStatusChangementDaoImpl.getInterfaceForceBackReason(interfaceIds);
		if(forceBackReason != null){
			if(forceBackReason.size() == 0){
				retMap.put("retCode", 301);
				retMap.put("forceBackReason", null);
				retMap.put("retDesc", "没有查询到强制回收信息");
				return retMap;
			}
			Map<String, UserModel> userMap = new HashMap<String, UserModel>();
			List<UserModel> userList = productUserDaoImpl.selectAllUserFromTB_IDOC_USER();
			if(userList != null && userList.size() > 0){
				for(UserModel user : userList){
					userMap.put(String.valueOf(user.getUserId()), user);
				}
			}
			// 设置接口状态改变的操作者
			for(InterfaceStatusChangement changement : forceBackReason){
				String operatorId = String.valueOf(changement.getOperatorId());
				if(!StringUtil.isEmpty(operatorId) && userMap.containsKey(operatorId)){
					changement.setOperator(userMap.get(operatorId));
				}
			}
			retMap.put("retCode", 200);
			retMap.put("forceBackReason", forceBackReason);
		}else{
			retMap.put("retCode", 401);
			retMap.put("retDesc", "获取接口强制回收原因错误");
		}
		return retMap;
	}
	
	@RequestMapping("/idoc/statistics/exportInterInfo2Excel.html")
	public ResponseEntity<byte[]> download(HttpServletRequest request, 
			@RequestParam("interfaceIds") String interfaceIds,
			@RequestParam("projectId") String projectId,
			@RequestParam("createFileOnServer") boolean createFileOnServer) throws IOException {
		if(StringUtil.isEmpty(interfaceIds)){
			return new ResponseEntity<byte[]>("请求的参数为空，生成接口信息文档失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		String[] ids = interfaceIds.split(",");
		if(ids.length <= 0){
			return new ResponseEntity<byte[]>("没有选定要导出的接口，生成接口信息文档失败！".getBytes("GBK"), HttpStatus.CREATED);
		}
		List<String> interfaceIdList = Arrays.asList(ids);
		byte[] flieByteStream = null;
		String path = null;
		if(createFileOnServer){ // 在服务器端生成文件
			path = exportInterInfo2ExcelServiceImpl.exportInterInfo2Excel(projectId, interfaceIdList);
			if(StringUtil.isEmpty(path)){
				return new ResponseEntity<byte[]>("后台自动生成接口信息文档失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
			}
			File file = new File(path);
			flieByteStream = FileUtil.readAsByteArray(file);
		}else{
			flieByteStream = exportInterInfo2ExcelServiceImpl.exportInterInfo2ByteStream(projectId, interfaceIdList);
		}
		if(flieByteStream == null){
			return new ResponseEntity<byte[]>("后台自动生成接口信息文档失败，请联系管理员！".getBytes("GBK"), HttpStatus.CREATED);
		}
        HttpHeaders headers = new HttpHeaders();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = "接口信息统计-" + sdf.format(new Date()) + ".xls";
        String fileName = new String(name.getBytes("UTF-8"),"iso-8859-1"); //为了解决中文名称乱码问题  
        headers.setContentDispositionFormData("attachment", fileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return new ResponseEntity<byte[]>(flieByteStream, headers, HttpStatus.CREATED);
	}
}