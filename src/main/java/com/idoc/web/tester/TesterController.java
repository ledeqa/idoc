package com.idoc.web.tester;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.idoc.model.Interface;
import com.idoc.service.docManage.InterfaceServiceImpl;
import com.idoc.web.BaseController;

@Controller
@RequestMapping("/idoc/tester")
public class TesterController extends BaseController {

	
	@Autowired
	private InterfaceServiceImpl interfaceServiceImpl;
	
	@RequestMapping("index.html")
	public String index(@RequestParam("interfaceId") Long interfaceId,
					@RequestParam("projectId")String projectId,
					Model model){
		Interface inter = interfaceServiceImpl.getInterfaceInfo(interfaceId);
		model.addAttribute("inter", inter);
		model.addAttribute("projectId", projectId);
		
		return "/tester/index";
	}
}
