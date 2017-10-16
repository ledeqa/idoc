package com.idoc.web.countOnlinePeople;

import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.idoc.util.CountSession;
import com.idoc.web.BaseController;
@Controller
public class CountOnlinePeopleController extends BaseController{
      @RequestMapping("/getOnlinePeopleCount")
      @ResponseBody
      public Map<String,Object> getOnlinePeopleCount(){
    	  Map<String,Object> retMap = new HashMap<String,Object>();
    	  retMap.put("SessionCount", CountSession.onlinePeople.get());
//    	  retMap.put("historyPeople", CountSession.historyPeople.get());
    	  return retMap;
      }
}
