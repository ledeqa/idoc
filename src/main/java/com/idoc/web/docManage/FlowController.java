package com.idoc.web.docManage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.dao.docManage.InterfaceDaoImpl;
import com.idoc.dao.login.LoginDaoImpl;
import com.idoc.form.InterfaceForm;
import com.idoc.model.Interface;
import com.idoc.model.Module;
import com.idoc.model.ProjectModel;
import com.idoc.model.Role;
import com.idoc.model.UserModel;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.service.docManage.ModuleServiceImpl;
import com.idoc.service.flow.impl.StatusFlowServiceImpl;
import com.idoc.service.productAndProject.ProductAndProjectServiceImpl;
import com.idoc.service.role.RoleConfigManagementService;
import com.idoc.web.BaseController;

@Controller("flowController")
@RequestMapping("/idoc/flow")
public class FlowController extends BaseController{
	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@Autowired
	private ModuleServiceImpl moduleServiceImpl;
	
	@Autowired
	private LoginDaoImpl loginDaoImpl;
	
	@Autowired
	private InterfaceDaoImpl interfaceDaoImpl;
	
	@Autowired
	private RoleConfigManagementService roleConfigManagementService;
	
	@Autowired
	private ProductAndProjectServiceImpl productAndProjectServiceImpl;
	
	@Autowired
	private StatusFlowServiceImpl statusFlowServiceImpl;
	
	@RequestMapping("index.html")
	public String index(@RequestParam(value = "projectId")String projectId, 
			@RequestParam(value = "interfaceId")String interfaceId,
			 HttpServletRequest request, Model model){
		List<Module> modules = moduleServiceImpl.getFullModuleInfoListByProjectId(projectId);
		Interface interf = interfaceServiceImpl.getInterfaceInfo(Long.valueOf(interfaceId));
		String englishName = (String) request.getSession().getAttribute("englishName");
		ProjectModel project = productAndProjectServiceImpl.selectProjectModelByProjectId(Long.valueOf(projectId));
		Role role = null;
		if(project != null){
			role = roleConfigManagementService.selectRoleByUserEnglishName(englishName, project.getProductId());
		}
		String roleName = null;
		if(role != null){
			roleName = role.getRoleName();
		}
		UserModel creator = loginDaoImpl.queryUserModelByUserId(interf.getCreatorId());
		String creatorName = null;
		if(creator != null){
			creatorName = creator.getUserName();
		}
		model.addAttribute("modules", modules);
		model.addAttribute("projectId", projectId);
		model.addAttribute("interface", interf);
		model.addAttribute("role", roleName);
		model.addAttribute("creatorName", creatorName);
		return "/flow/index";
	}
	
	@RequestMapping("audit.html")
	@ResponseBody
	public Map<String, Object> audit(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "发起审核", "审核中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "发起审核时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.audit(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "发起审核失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "发起审核成功");
		}
		return retMap;
	}
	
	@RequestMapping("revise.html")
	@ResponseBody
	public Map<String, Object> revise(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId, @RequestParam(value = "proposal")String proposal){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "建议修改", "编辑中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "建议修改时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.revise(Long.valueOf(projectId), Long.valueOf(interfaceId), proposal);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "建议修改失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "建议修改成功");
		}
		return retMap;
	}
	
	@RequestMapping("auditSuccess.html")
	@ResponseBody
	public Map<String, Object> auditSuccess(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId,
	@RequestParam(value = "interStatus")String interStatus){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status;
		if(Integer.parseInt(interStatus) == 3){
			status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "审核通过", "审核通过", 1, null);
		}else if(Integer.parseInt(interStatus) == 310){
			status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "前端审核通过", "前端审核通过", 1, null);
		}else{
			status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "客户端审核通过", "客户端审核通过", 1, null);
		}
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "审核通过时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.auditSuccess(Long.valueOf(projectId), Long.valueOf(interfaceId), Long.valueOf(interStatus));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "审核通过失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "审核通过成功");
			retMap.put("status", status);
		}
		return retMap;
	}
	
	@RequestMapping("forceBack.html")
	@ResponseBody
	public Map<String, Object> forceBack(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId, @RequestParam(value = "reason")int reason,
	@RequestParam(value = "remark")String remark){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "强制回收", "编辑中", reason, remark);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "强制回收时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.forceBackToEdit(Long.valueOf(projectId), Long.valueOf(interfaceId), reason);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "强制回收失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "强制回收成功");
		}
		return retMap;
	}
	
	@RequestMapping("iteration.html")
	@ResponseBody
	public Map<String, Object> iteration(
			@RequestParam(value = "projectId")String projectId, 
			@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.iterationInter(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "版本迭代失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "版本迭代成功");
		}
		return retMap;
	}
	
	@RequestMapping("submitToTest.html")
	@ResponseBody
	public Map<String, Object> submitToTest(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId,@RequestParam(value = "flag")Integer flag){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "提交测试", "已提测", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "提交测试时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.submitToTest(Long.valueOf(projectId), Long.valueOf(interfaceId),flag);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "提交测试失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "提交测试成功");
		}
		return retMap;
	}
	
	@RequestMapping("test.html")
	@ResponseBody
	public Map<String, Object> test(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "开始测试", "测试中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "开始测试时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.startToTest(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "开始测试失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "开始测试成功");
		}
		return retMap;
	}
	
	@RequestMapping("tested.html")
	@ResponseBody
	public Map<String, Object> tested(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId,@RequestParam(value = "flag")Integer flag){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "测试完成", "测试完成", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "测试完成时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.tested(Long.valueOf(projectId), Long.valueOf(interfaceId),flag);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "测试完成失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "测试完成成功");
		}
		return retMap;
	}
	
	@RequestMapping("returnToTest.html")
	@ResponseBody
	public Map<String, Object> returnToTest(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "返回测试", "测试中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "返回测试时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.returnToTest(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "返回测试失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "返回测试成功");
		}
		return retMap;
	}
	
	@RequestMapping("pressure.html")
	@ResponseBody
	public Map<String, Object> pressure(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "提交压测", "压测中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "提交压测时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.pressureTest(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "提交压测失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "提交压测成功");
		}
		return retMap;
	}
	
	@RequestMapping("pressured.html")
	@ResponseBody
	public Map<String, Object> pressured(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "压测完成", "压测完成", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "压测完成时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.pressureTestFinished(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "压测完成失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "压测完成成功");
		}
		return retMap;
	}
	
	@RequestMapping("returnToPressure.html")
	@ResponseBody
	public Map<String, Object> returnToPressure(@RequestParam(value = "projectId")String projectId, 
	@RequestParam(value = "interfaceId")String interfaceId){
		Map<String, Object> retMap = new HashMap<String, Object>();
		int status = statusFlowServiceImpl.insertInterfaceStatusChangement(Long.valueOf(interfaceId), "返回压测", "压测中", 1, null);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "返回压测时状态改变出错");
			return retMap;
		}
		status = statusFlowServiceImpl.returnToPressureTest(Long.valueOf(projectId), Long.valueOf(interfaceId));
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "返回压测失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "返回压测成功");
		}
		return retMap;
	}
	
	@RequestMapping("online.html")
	@ResponseBody
	public Map<String, Object> online(@RequestBody InterfaceForm form){
		Map<String, Object> retMap = new HashMap<String, Object>();
		/*int status = statusFlowServiceImpl.insertInterfaceStatusChangement(form.getInterfaceId(), "上线", "上线", 1);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "上线时状态改变出错");
			return retMap;
		}*/
		int status = statusFlowServiceImpl.online(form.getProjectId(), form.getInterfaceId(), form);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "上线失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "上线成功");
		}
		return retMap;
	}
	
	@RequestMapping("savePeopleInfo.html")
	@ResponseBody
	// type为保存信息类型，1-需求人员，2-前端开发人员，3-客户端开发人员，4-测试人员
	public Map<String, Object> savePeopleInfo(@RequestParam(value = "type")int type, 
	@RequestParam(value = "interfaceId")String interfaceId, @RequestParam(value = "peopleIds")String peopleIds){
		Map<String, Object> retMap = new HashMap<String, Object>();
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("interfaceId", interfaceId);
		if(type == 1){
			map.put("reqPeopleIds", peopleIds);
		}else if(type == 2){
			map.put("frontPeopleIds", peopleIds);
		}else if(type == 3){
			map.put("clientPeopleIds", peopleIds);
		}else if(type == 4){
			map.put("testPeopleIds", peopleIds);
		}else if(type == 5){
			map.put("behindPeopleIds", peopleIds);
		}
		int status = interfaceDaoImpl.updateInterface(map);
		if(status == 0){
			retMap.put("retCode", 400);
			retMap.put("retDesc", "更新失败");
		}else{
			retMap.put("retCode", 200);
			retMap.put("retDesc", "更新成功");
		}
		return retMap;
	}
}