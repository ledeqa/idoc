package com.idoc.web;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.constant.LogConstant;
import com.idoc.dao.cron.CronDao;
import com.idoc.service.login.LoginIndexServiceImpl;
import com.idoc.util.LoginUserInfoUtil;
import com.netease.cron.Constant;
import com.netease.cron.CronModel;


@Controller
@RequestMapping("/cron")
public class CronController extends BaseController {
	
	@Autowired
	private CronDao cronDao;
	
	@Resource(name = "loginIndexServiceImpl")
	private LoginIndexServiceImpl loginIndexServiceImpl;
	
	@RequestMapping("/cronList")
	public String cronList(Model model){
		LogConstant.debugLog.info(Constant.CRON_GROUP_NAME);
		String groupName = Constant.CONFIG.getStringProperty(Constant.CRON_GROUP_NAME);
		
		List<CronModel> cronList = this.cronDao.getAllCron(groupName);
		model.addAttribute("cronList", cronList);
		
		String englishName = LoginUserInfoUtil.getUserEnglishName();
		if(englishName != null ){
			int adminFlag = loginIndexServiceImpl.confirmAdmin(englishName);
			if(adminFlag ==1 ){
				return "/cron/cronList";
			}else{
				return "redirect:/index.html";
			}
		}
		
		return "redirect:/index.html";
	}
	
	@RequestMapping("/create")
	public String createCron(Model model){
		model.addAttribute("cron", new CronModel());
		model.addAttribute("action", "create");
		
		return "/cron/editCron";
	}
	
	@RequestMapping("/edit")
	public String editCron(Model model, String id){
		
		CronModel cron = this.cronDao.selectCron(id);
		model.addAttribute("cron", cron);
		
		model.addAttribute("action", "edit");
		
		return "/cron/editCron";
	}
	
	@RequestMapping(value = "/save", method = RequestMethod.POST)
	@ResponseBody
	public void saveCron(CronModel cronModel){
		if(StringUtils.isNotBlank(cronModel.getId())){
			cronDao.updateCron(cronModel);
		}else{
			cronDao.addCron(cronModel);
		}
	}
	
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public String delete(@PathVariable("id") String id) {
		cronDao.deleteCron(id);
		
		return "redirect:/cron/cronList.html";
	}
}
